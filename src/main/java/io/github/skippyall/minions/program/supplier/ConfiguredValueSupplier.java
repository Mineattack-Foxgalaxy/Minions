package io.github.skippyall.minions.program.supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.conversion.Casts;
import io.github.skippyall.minions.program.conversion.ConverterList;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.program.handler.ConfiguredValueHandler;
import io.github.skippyall.minions.program.handler.Parameter;
import io.github.skippyall.minions.program.handler.ParameterValueList;
import io.github.skippyall.minions.registration.ResolutionContext;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class ConfiguredValueSupplier<P> extends ConfiguredValueHandler<P, ValueSupplier> {
    public static final Codec<ConfiguredValueSupplier<?>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Parameter.CODEC.fieldOf("parameter").forGetter(e -> e.parameter),
            ValueSupplier.CODEC.fieldOf("argument").forGetter(e -> e.handler),
            ConverterList.CODEC.fieldOf("converter").forGetter(e -> e.converters)
    ).apply(instance, ConfiguredValueSupplier::new));

    public ConfiguredValueSupplier(Parameter<P> parameter, ValueSupplier supplier, ConverterList converters) {
        super(parameter, supplier, converters);
    }

    public ValueSupplier getSupplier() {
        return handler;
    }

    public void setSupplier(ValueSupplier supplier) {
        this.handler = supplier;
    }

    void addToList(ParameterValueList list, Context context, Consumer<Component> errorConsumer) {
        Result<P, Component> result = getValue(handler, context);
        switch (result) {
            case Result.Success<P, Component> success -> list.setValue(parameter, success.result());
            case Result.Error<P, Component> error -> {
                errorConsumer.accept(Component.translatable("minions.instruction.argument.error", parameter.name()));
                errorConsumer.accept(error.message());
            }
        }
    }

    private Result<P, Component> getValue(ValueSupplier supplier, Context context) {
        Context newContext = context.toBuilder()
                .put(ResolutionContext.PARAMETER_NAME, parameter.name())
                .build();

        Result<TypedValue<?>, Component> value = supplier.resolve(newContext);
        Result<TypedValue<?>, Component> convertedResult = value.flatMap(v -> converters.convert(v));

        return convertedResult.flatMap(convertedValue -> Casts.castOrError(convertedValue, parameter.type()));
    }
}

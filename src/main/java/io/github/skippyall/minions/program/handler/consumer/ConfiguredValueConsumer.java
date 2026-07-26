package io.github.skippyall.minions.program.handler.consumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.conversion.ConverterList;
import io.github.skippyall.minions.program.handler.ConfiguredValueHandler;
import io.github.skippyall.minions.program.handler.Parameter;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.registration.ResolutionContext;
import net.minecraft.network.chat.Component;

public class ConfiguredValueConsumer<P> extends ConfiguredValueHandler<P, ValueConsumer> {
    public static final Codec<ConfiguredValueConsumer<?>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Parameter.CODEC.fieldOf("parameter").forGetter(e -> e.parameter),
            ValueConsumer.CODEC.fieldOf("argument").forGetter(e -> e.handler),
            ConverterList.CODEC.fieldOf("converter").forGetter(e -> e.converters)
    ).apply(instance, ConfiguredValueConsumer::new));

    public ConfiguredValueConsumer(Parameter<P> parameter, ValueConsumer supplier, ConverterList converters) {
        super(parameter, supplier, converters);
    }

    public ValueConsumer getConsumer() {
        return handler;
    }

    public void setConsumer(ValueConsumer consumer) {
        this.handler = consumer;
    }

    public void consumeValue(TypedValue<?> value, Context context) {
        Result<TypedValue<?>, Component> converted = converters.convert(value);
        if(converted instanceof Result.Success<TypedValue<?>, Component>) {
            Context newContext = context.toBuilder()
                    .put(ResolutionContext.PARAMETER_NAME, parameter.name())
                    .build();

            handler.consume(value, newContext);
        }
    }
}

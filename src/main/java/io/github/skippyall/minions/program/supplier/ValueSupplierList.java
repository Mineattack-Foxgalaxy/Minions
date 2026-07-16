package io.github.skippyall.minions.program.supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.conversion.Casts;
import io.github.skippyall.minions.program.conversion.ConverterList;
import io.github.skippyall.minions.program.instruction.InstructionType;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.registration.ResolutionContext;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ValueSupplierList {
    public static final Codec<ValueSupplierList> CODEC = ValueSupplierEntry.CODEC
            .listOf()
            .xmap(ValueSupplierList::new, ValueSupplierList::toCodecList);

    private final Map<Parameter<?>, ValueSupplierEntry<?>> arguments = new HashMap<>();
    private final List<Consumer<Parameter<?>>> changeListeners = new ArrayList<>();

    public ValueSupplierList() {

    }

    private ValueSupplierList(List<ValueSupplierEntry<?>> arguments) {
        for(ValueSupplierEntry<?> argument : arguments) {
            this.arguments.put(argument.parameter, argument);
        }
    }

    private List<ValueSupplierEntry<?>> toCodecList() {
        return List.copyOf(arguments.values());
    }

    public @Nullable ValueSupplier getArgument(Parameter<?> parameter) {
        if(arguments.containsKey(parameter)) {
            return arguments.get(parameter).supplier;
        } else {
            return null;
        }
    }

    @Nullable
    public <P> ValueSupplierEntry<P> getEntry(Parameter<P> parameter) {
        //noinspection unchecked
        return (ValueSupplierEntry<P>) arguments.get(parameter);
    }

    public <P> ValueSupplierEntry<P> createEntry(Parameter<P> parameter, ValueSupplier supplier) {
        ValueSupplierEntry<P> entry = new ValueSupplierEntry<>(parameter, supplier, new ConverterList());
        arguments.put(parameter, entry);
        return entry;
    }

    public void removeEntry(Parameter<?> parameter) {
        arguments.remove(parameter);
    }

    public ParameterValueList resolve(Context context, Consumer<Component> errorConsumer) {
        ParameterValueList list = new ParameterValueList();
        for(ValueSupplierEntry<?> argument : arguments.values()) {
            argument.addToList(list, context, errorConsumer);
        }
        return list;
    }

    public boolean hasArgumentFor(Parameter<?> parameter) {
        return arguments.containsKey(parameter);
    }

    public void checkHasArguments(Collection<Parameter<?>> checkParameters, Consumer<Component> errorConsumer) {
        for(Parameter<?> parameter : checkParameters) {
            if(!hasArgumentFor(parameter)) {
                errorConsumer.accept(Component.translatable("minions.gui.instruction.check.argument_not_set", parameter.name()));
            }
        }
    }

    public void checkRun(InstructionType instructionType, Consumer<Component> errorConsumer) {
        checkHasArguments(instructionType.getParameters(), errorConsumer);

        for(ValueSupplierEntry<?> entry : arguments.values()) {
            entry.check(errorConsumer);
        }
    }

    private void onChange(Parameter<?> parameter) {
        for (Consumer<Parameter<?>> listener : changeListeners) {
            listener.accept(parameter);
        }
    }

    public void addListener(Consumer<Parameter<?>> listener) {
        changeListeners.add(listener);
    }

    public void removeListener(Consumer<Parameter<?>> listener) {
        changeListeners.remove(listener);
    }

    public static class ValueSupplierEntry<P> {
        public static final Codec<ValueSupplierEntry<?>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Parameter.CODEC.fieldOf("parameter").forGetter(e -> e.parameter),
                ValueSupplier.CODEC.fieldOf("argument").forGetter(e -> e.supplier),
                ConverterList.CODEC.fieldOf("converter").forGetter(e -> e.converters)
        ).apply(instance, ValueSupplierEntry::new));

        private Parameter<P> parameter;
        private ValueSupplier supplier;
        private ConverterList converters;

        public ValueSupplierEntry(Parameter<P> parameter, ValueSupplier supplier, ConverterList converters) {
            this.parameter = parameter;
            this.supplier = supplier;
            this.converters = converters;
        }

        public ConverterList getConverters() {
            return converters;
        }

        public Parameter<P> getParameter() {
            return parameter;
        }

        public ValueSupplier getSupplier() {
            return supplier;
        }

        public void setParameter(Parameter<P> parameter) {
            this.parameter = parameter;
        }

        public void setSupplier(ValueSupplier supplier) {
            this.supplier = supplier;
        }

        private void addToList(ParameterValueList list, Context context, Consumer<Component> errorConsumer) {
            Result<P, Component> result = getValue(supplier, context);
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

        public void check(Consumer<Component> errorConsumer) {
            //converters.check(errorConsumer, parameter.type(), supplier.getValueType());
        }
    }
}

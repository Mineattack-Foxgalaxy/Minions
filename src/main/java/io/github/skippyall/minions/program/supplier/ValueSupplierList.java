package io.github.skippyall.minions.program.supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.conversion.Casts;
import io.github.skippyall.minions.program.conversion.ConverterList;
import io.github.skippyall.minions.program.instruction.InstructionType;
import io.github.skippyall.minions.program.value.TypedValue;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ValueSupplierList<R extends InstructionRuntime<R>> {
    private final Map<Parameter<?>, ValueSupplierEntry<?,R>> arguments = new HashMap<>();
    private final List<Consumer<Parameter<?>>> changeListeners = new ArrayList<>();

    public ValueSupplierList() {

    }

    private ValueSupplierList(List<ValueSupplierEntry<?,R>> arguments) {
        for(ValueSupplierEntry<?,R> argument : arguments) {
            this.arguments.put(argument.parameter, argument);
        }
    }

    private List<ValueSupplierEntry<?,R>> toCodecList() {
        return List.copyOf(arguments.values());
    }

    public @Nullable ValueSupplier<?,R> getArgument(Parameter<?> parameter) {
        if(arguments.containsKey(parameter)) {
            return arguments.get(parameter).supplier;
        } else {
            return null;
        }
    }

    public <P> ValueSupplierEntry<P,R> getEntry(Parameter<P> parameter) {
        //noinspection unchecked
        return (ValueSupplierEntry<P,R>) arguments.get(parameter);
    }

    public <P> ValueSupplierEntry<P,R> createEntry(Parameter<P> parameter, ValueSupplier<?,R> supplier) {
        ValueSupplierEntry<P,R> entry = new ValueSupplierEntry<>(parameter, supplier, new ConverterList());
        arguments.put(parameter, entry);
        return entry;
    }

    public void removeEntry(Parameter<?> parameter) {
        arguments.remove(parameter);
    }

    public ParameterValueList resolve(R runtime, Consumer<Component> errorConsumer) {
        ParameterValueList list = new ParameterValueList();
        for(ValueSupplierEntry<?,R> argument : arguments.values()) {
            argument.addToList(list, runtime, errorConsumer);
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

    public void checkRun(InstructionType<R> instructionType, Consumer<Component> errorConsumer) {
        checkHasArguments(instructionType.getParameters(), errorConsumer);

        for(ValueSupplierEntry<?,R> entry : arguments.values()) {
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

    public static <R extends InstructionRuntime<R>> Codec<ValueSupplierList<R>> getCodec(Codec<ValueSupplier<?,R>> argumentCodec) {
        return ValueSupplierEntry.getCodec(argumentCodec)
                .codec()
                .listOf()
                .xmap(ValueSupplierList::new, ValueSupplierList::toCodecList);
    }

    public static class ValueSupplierEntry<P, R extends InstructionRuntime<R>> {
        private Parameter<P> parameter;
        private ValueSupplier<?,R> supplier;
        private ConverterList converters;

        public ValueSupplierEntry(Parameter<P> parameter, ValueSupplier<?, R> supplier, ConverterList converters) {
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

        public ValueSupplier<?, R> getSupplier() {
            return supplier;
        }

        public void setParameter(Parameter<P> parameter) {
            this.parameter = parameter;
        }

        public void setSupplier(ValueSupplier<?, R> supplier) {
            this.supplier = supplier;
        }

        private void addToList(ParameterValueList list, R runtime, Consumer<Component> errorConsumer) {
            Result<P, Component> result = getValue(supplier, runtime);
            switch (result) {
                case Result.Success<P, Component> success -> list.setValue(parameter, success.result());
                case Result.Error<P, Component> error -> {
                    errorConsumer.accept(Component.translatable("minions.instruction.argument.error", parameter.name()));
                    errorConsumer.accept(error.message());
                }
            }
        }

        private <S> Result<P, Component> getValue(ValueSupplier<S, R> supplier, R runtime) {
            S value = supplier.resolve(runtime);
            Result<TypedValue<?>, Component> convertedResult = converters.convert(new TypedValue<>(value, supplier.getValueType()));

            return convertedResult.flatMap(convertedValue -> Casts.castOrError(convertedValue, parameter.type()));
        }

        public void check(Consumer<Component> errorConsumer) {
            converters.check(errorConsumer, parameter.type(), supplier.getValueType());
        }

        public static <R extends InstructionRuntime<R>> MapCodec<ValueSupplierEntry<?,R>> getCodec(Codec<ValueSupplier<?,R>> argumentCodec) {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Parameter.CODEC.fieldOf("parameter").forGetter(e -> e.parameter),
                    argumentCodec.fieldOf("argument").forGetter(e -> e.supplier),
                    ConverterList.CODEC.fieldOf("converter").forGetter(e -> e.converters)
            ).apply(instance, ValueSupplierEntry::new));
        }
    }
}

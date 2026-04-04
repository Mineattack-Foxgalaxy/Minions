package io.github.skippyall.minions.program.supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.conversion.Cast;
import io.github.skippyall.minions.program.conversion.Casts;
import io.github.skippyall.minions.program.conversion.ValueConverter;
import io.github.skippyall.minions.program.instruction.InstructionType;
import io.github.skippyall.minions.program.value.ValueType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ValueSupplierList<R extends InstructionRuntime<R>> {
    private final Map<Parameter<?>, ValueSupplierEntry<?,?,R>> arguments = new HashMap<>();
    private final List<Consumer<Parameter<?>>> changeListeners = new ArrayList<>();

    public ValueSupplierList() {

    }

    private ValueSupplierList(List<ValueSupplierEntry<?,?,R>> arguments) {
        for(ValueSupplierEntry<?,?,R> argument : arguments) {
            this.arguments.put(argument.parameter, argument);
        }
    }

    private List<ValueSupplierEntry<?,?,R>> toCodecList() {
        return List.copyOf(arguments.values());
    }

    public <T> T getValue(Parameter<T> parameter, R runtime) {
        ValueSupplierEntry<?,?,R> entry = getEntry(parameter);
        return parameter.type().checkedCast(entry.getValue(runtime));
    }

    public ValueSupplier<?,R> getArgument(Parameter<?> parameter) {
        if(arguments.containsKey(parameter)) {
            return arguments.get(parameter).supplier;
        } else {
            return null;
        }
    }

    public <P> ValueSupplierEntry<P,?,R> getEntry(Parameter<P> parameter) {
        //noinspection unchecked
        return (ValueSupplierEntry<P, ?, R>) arguments.get(parameter);
    }

    public void setArgument(Parameter<?> parameter, ValueSupplier<?,R> valueSupplier) {
        arguments.put(parameter, new ValueSupplierEntry<>(parameter, valueSupplier, List.of()));
        onChange(parameter);
    }

    public void setArgument(Parameter<?> parameter, ValueSupplier<?,R> valueSupplier, List<ValueConverter<?, ?>> converter) {
        arguments.put(parameter, new ValueSupplierEntry<>(parameter, valueSupplier, converter));
        onChange(parameter);
    }

    public boolean hasArgumentFor(Parameter<?> parameter) {
        return arguments.containsKey(parameter);
    }

    public Result<@Nullable Void, Text> checkHasArguments(Collection<Parameter<?>> checkParameters) {
        for(Parameter<?> parameter : checkParameters) {
            if(!hasArgumentFor(parameter)) {
                return new Result.Error<>(Text.translatable("minions.gui.instruction.check.argument_not_set", parameter.name()));
            }
        }
        return new Result.Success<>(null);
    }

    public Result<@Nullable Void, Text> checkRun(InstructionType<R> instructionType) {
        Result<@Nullable Void, Text> checkResult = checkHasArguments(instructionType.getParameters());
        if(!checkResult.isSuccess()) {
            return checkResult;
        }

        for(ValueSupplierEntry<?,?,R> entry : arguments.values()) {
            checkResult = entry.check();
            if(!checkResult.isSuccess()) {
                return checkResult;
            }
        }
        return new Result.Success<>(null);
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

    public static class ValueSupplierEntry<S, P, R extends InstructionRuntime<R>> {
        private Parameter<P> parameter;
        private ValueSupplier<S,R> supplier;
        private List<ValueConverter<?,?>> converters;

        public ValueSupplierEntry(Parameter<P> parameter, ValueSupplier<S, R> supplier, Collection<ValueConverter<?, ?>> converters) {
            this.parameter = parameter;
            this.supplier = supplier;
            this.converters = new LinkedList<>(converters);
        }

        public List<ValueConverter<?,?>> getConverters() {
            return converters;
        }

        public Parameter<P> getParameter() {
            return parameter;
        }

        public ValueSupplier<S, R> getSupplier() {
            return supplier;
        }

        public void setParameter(Parameter<P> parameter) {
            this.parameter = parameter;
        }

        public void setSupplier(ValueSupplier<S, R> supplier) {
            this.supplier = supplier;
        }

        public void setConverters(List<ValueConverter<?, ?>> converters) {
            this.converters = converters;
        }

        public @Nullable P getValue(R runtime) {
            S value = supplier.resolve(runtime);
            Iterator<ValueConverter<?,?>> iterator = converters.iterator();
            Object convertedValue = convert(supplier.getValueType(), value, iterator.next(), iterator);

            return finalCast(convertedValue);
        }

        private <F> @Nullable P finalCast(Object value) {
            //noinspection unchecked
            ValueType<F> lastConvertedType = (ValueType<F>) converters.getLast().getTo();
            F convertedValue = lastConvertedType.checkedCast(value);
            if(convertedValue == null) {
                return null;
            }

            Cast<F,P> cast = Casts.getCast(lastConvertedType, parameter.type());
            if(cast != null) {
                return cast.cast(convertedValue);
            }
            return null;
        }

        private <F,I,T> @Nullable Object convert(ValueType<F> fromType, F from, ValueConverter<I,T> converter, Iterator<ValueConverter<?,?>> iterator) {
            Cast<F, I> cast = Casts.getCast(fromType, converter.getFrom());
            if(cast == null) {
                return null;
            }
            I inter = cast.cast(from);
            if(inter == null) {
                return null;
            }
            T to = converter.convert(inter);

            if(iterator.hasNext() && to != null) {
                return convert(converter.getTo(), to, iterator.next(), iterator);
            } else {
                return to;
            }
        }

        public Result<@Nullable Void, Text> check() {
            return new Result.Success<>(null);
        }

        public static <R extends InstructionRuntime<R>> MapCodec<ValueSupplierEntry<?,?,R>> getCodec(Codec<ValueSupplier<?,R>> argumentCodec) {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Parameter.CODEC.fieldOf("parameter").forGetter(e -> e.parameter),
                    argumentCodec.fieldOf("argument").forGetter(e -> e.supplier),
                    ValueConverter.CODEC.listOf().optionalFieldOf("converter", List.of()).forGetter(e -> e.converters)
            ).apply(instance, ValueSupplierEntry::new));
        }
    }
}

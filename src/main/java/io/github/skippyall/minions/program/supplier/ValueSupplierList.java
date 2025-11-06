package io.github.skippyall.minions.program.supplier;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.InstructionRuntime;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ValueSupplierList<R extends InstructionRuntime<R>> {
    private final Map<String, ValueSupplier<?, R>> arguments;

    public ValueSupplierList() {
        arguments = new HashMap<>();
    }

    public ValueSupplierList(Map<String, ValueSupplier<?,R>> arguments) {
        this.arguments = new HashMap<>(arguments);
    }

    public <T> T getValue(Parameter<T> parameter, R runtime) {
        ValueSupplier<T,R> valueSupplier = getArgument(parameter);
        return valueSupplier != null ? valueSupplier.resolve(runtime) : null;
    }

    public <T, A extends ValueSupplier<T,R>> A getArgument(Parameter<T> parameter) {
        ValueSupplier<?,R> valueSupplier = arguments.get(parameter.name());
        return valueSupplier == null ? null : valueSupplier.cast(parameter.type());
    }

    public <T> void setArgument(Parameter<T> parameter, ValueSupplier<T,R> valueSupplier) {
        arguments.put(parameter.name(), valueSupplier);
    }

    public boolean hasArgumentFor(Parameter<?> parameter) {
        return getArgument(parameter) != null;
    }

    public boolean hasArgumentForAll(Collection<Parameter<?>> checkParameters) {
        for(Parameter<?> parameter : checkParameters) {
            if(!hasArgumentFor(parameter)) {
                return false;
            }
        }
        return true;
    }

    public static <R extends InstructionRuntime<R>> Codec<ValueSupplierList<R>> getCodec(Codec<ValueSupplier<?,R>> argumentCodec) {
        return Codec.unboundedMap(Codec.STRING, argumentCodec)
                .xmap(ValueSupplierList::new, list -> list.arguments);
    }
}

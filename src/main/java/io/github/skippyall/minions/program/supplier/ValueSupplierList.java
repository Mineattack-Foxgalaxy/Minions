package io.github.skippyall.minions.program.supplier;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.value.Cast;
import io.github.skippyall.minions.program.value.Casts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ValueSupplierList<R extends InstructionRuntime<R>> {
    private final Map<String, ValueSupplier<?, R>> arguments;
    private final List<Consumer<Parameter<?>>> changeListeners = new ArrayList<>();

    public ValueSupplierList() {
        arguments = new HashMap<>();
    }

    public ValueSupplierList(Map<String, ValueSupplier<?,R>> arguments) {
        this.arguments = new HashMap<>(arguments);
    }

    public <F, T> T getValue(Parameter<T> parameter, R runtime) {
        //noinspection unchecked
        ValueSupplier<F,R> valueSupplier = (ValueSupplier<F, R>) getArgument(parameter);
        if(valueSupplier == null) {
            return null;
        }

        if(valueSupplier.getValueType() == parameter.type()) {
            return valueSupplier.cast(parameter.type()).resolve(runtime);
        } else {
            Cast<F,T> cast = Casts.getCast(valueSupplier.getValueType(), parameter.type());
            if(cast != null) {
                return cast.cast(valueSupplier.resolve(runtime));
            }
        }
        return null;
    }

    public ValueSupplier<?,R> getArgument(Parameter<?> parameter) {
        return arguments.get(parameter.name());
    }

    public <T> void setArgument(Parameter<T> parameter, ValueSupplier<?,R> valueSupplier) {
        arguments.put(parameter.name(), valueSupplier);
        onChange(parameter);
    }

    public boolean hasArgumentFor(Parameter<?> parameter) {
        return arguments.containsKey(parameter.name());
    }

    public boolean hasArgumentForAll(Collection<Parameter<?>> checkParameters) {
        for(Parameter<?> parameter : checkParameters) {
            if(!hasArgumentFor(parameter)) {
                return false;
            }
        }
        return true;
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
        return Codec.unboundedMap(Codec.STRING, argumentCodec)
                .xmap(ValueSupplierList::new, list -> list.arguments);
    }
}

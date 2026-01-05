package io.github.skippyall.minions.program.consumer;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.supplier.Parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ValueConsumerList<R extends InstructionRuntime<R>> {
    private final Map<String, ValueConsumer<?, R>> valueConsumers;
    private final List<Consumer<Parameter<?>>> changeListeners = new ArrayList<>();

    public ValueConsumerList() {
        valueConsumers = new HashMap<>();
    }

    public ValueConsumerList(Map<String, ValueConsumer<?,R>> valueConsumers) {
        this.valueConsumers = new HashMap<>(valueConsumers);
    }

    public <T, A extends ValueConsumer<T,R>> A getValueConsumer(Parameter<T> parameter) {
        ValueConsumer<?,R> argument = valueConsumers.get(parameter.name());
        return argument == null ? null : argument.cast(parameter.type());
    }

    public <T> void setValueConsumer(Parameter<T> parameter, ValueConsumer<?,R> consumer) {
        valueConsumers.put(parameter.name(), consumer);
        onChange(parameter);
    }

    public <T> void consumeValue(Parameter<T> parameter, T value, R runtime) {
        ValueConsumer<T,R> consumer = getValueConsumer(parameter).cast(parameter.type());
        if (consumer != null) {
            consumer.consume(value, runtime);
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

    public static <R extends InstructionRuntime<R>> Codec<ValueConsumerList<R>> getCodec(Codec<ValueConsumer<?,R>> valueConsumerCodec) {
        return Codec.unboundedMap(Codec.STRING, valueConsumerCodec)
                .xmap(ValueConsumerList::new, list -> list.valueConsumers);
    }
}

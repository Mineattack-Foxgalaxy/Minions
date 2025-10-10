package io.github.skippyall.minions.program.returnvalue;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.argument.Parameter;

import java.util.HashMap;
import java.util.Map;

public class ValueConsumerList<R extends InstructionRuntime<R>> {
    private final Map<String, ValueConsumer<?, R>> valueConsumers;

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
    }

    public static <R extends InstructionRuntime<R>> Codec<ValueConsumerList<R>> getCodec(Codec<ValueConsumerType<R>> genericCodec) {
        return Codec.unboundedMap(Codec.STRING, ValueConsumers.createValueConsumersCodec(genericCodec))
                .xmap(ValueConsumerList::new, list -> list.valueConsumers);
    }
}

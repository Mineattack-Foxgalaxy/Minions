package io.github.skippyall.minions.program.handler.consumer;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.conversion.ConverterList;
import io.github.skippyall.minions.program.handler.Parameter;
import io.github.skippyall.minions.program.handler.ParameterValueList;
import io.github.skippyall.minions.program.handler.ValueHandlerList;
import io.github.skippyall.minions.program.value.TypedValue;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class ValueConsumerList extends ValueHandlerList<ValueConsumer, ConfiguredValueConsumer<?>> {
    public static final Codec<ValueConsumerList> CODEC = ConfiguredValueConsumer.CODEC
            .listOf()
            .xmap(ValueConsumerList::new, ValueConsumerList::toCodecList);

    public ValueConsumerList() {

    }

    private ValueConsumerList(List<ConfiguredValueConsumer<?>> arguments) {
        for(ConfiguredValueConsumer<?> argument : arguments) {
            this.arguments.put(argument.getParameter(), argument);
        }
    }

    private List<ConfiguredValueConsumer<?>> toCodecList() {
        return List.copyOf(arguments.values());
    }

    @Nullable
    @Override
    public <P> ConfiguredValueConsumer<P> getEntry(Parameter<P> parameter) {
        return (ConfiguredValueConsumer<P>) super.getEntry(parameter);
    }

    @Override
    public <P> ConfiguredValueConsumer<P> createEntry(Parameter<P> parameter, ValueConsumer supplier) {
        ConfiguredValueConsumer<P> entry = new ConfiguredValueConsumer<>(parameter, supplier, new ConverterList());
        arguments.put(parameter, entry);
        return entry;
    }

    public void consumeValues(ParameterValueList list, Context context) {
        for(ConfiguredValueConsumer<?> consumer : arguments.values()) {
            consumer.consumeValue(TypedValue.of(list.getValue(consumer.getParameter()), consumer.getParameter().type()), context);
        }
    }
}

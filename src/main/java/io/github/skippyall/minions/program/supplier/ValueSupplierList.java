package io.github.skippyall.minions.program.supplier;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.conversion.ConverterList;
import io.github.skippyall.minions.program.handler.Parameter;
import io.github.skippyall.minions.program.handler.ParameterValueList;
import io.github.skippyall.minions.program.handler.ValueHandlerList;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ValueSupplierList extends ValueHandlerList<ValueSupplier, ConfiguredValueSupplier<?>> {
    public static final Codec<ValueSupplierList> CODEC = ConfiguredValueSupplier.CODEC
            .listOf()
            .xmap(ValueSupplierList::new, ValueSupplierList::toCodecList);

    public ValueSupplierList() {

    }

    private ValueSupplierList(List<ConfiguredValueSupplier<?>> arguments) {
        for(ConfiguredValueSupplier<?> argument : arguments) {
            this.arguments.put(argument.getParameter(), argument);
        }
    }

    private List<ConfiguredValueSupplier<?>> toCodecList() {
        return List.copyOf(arguments.values());
    }

    @Nullable
    @Override
    public <P> ConfiguredValueSupplier<P> getEntry(Parameter<P> parameter) {
        return (ConfiguredValueSupplier<P>) super.getEntry(parameter);
    }

    @Override
    public <P> ConfiguredValueSupplier<P> createEntry(Parameter<P> parameter, ValueSupplier supplier) {
        ConfiguredValueSupplier<P> entry = new ConfiguredValueSupplier<>(parameter, supplier, new ConverterList());
        arguments.put(parameter, entry);
        return entry;
    }

    public ParameterValueList resolve(Context context, Consumer<Component> errorConsumer) {
        ParameterValueList list = new ParameterValueList();
        for(ConfiguredValueSupplier<?> argument : arguments.values()) {
            argument.addToList(list, context, errorConsumer);
        }
        return list;
    }
}

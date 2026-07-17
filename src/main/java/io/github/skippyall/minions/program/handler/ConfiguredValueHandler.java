package io.github.skippyall.minions.program.handler;

import io.github.skippyall.minions.program.conversion.ConverterList;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class ConfiguredValueHandler<P, H> {
    protected Parameter<P> parameter;
    protected H handler;
    protected ConverterList converters;

    public ConfiguredValueHandler(Parameter<P> parameter, H handler, ConverterList converters) {
        this.parameter = parameter;
        this.handler = handler;
        this.converters = converters;
    }

    public ConverterList getConverters() {
        return converters;
    }

    public Parameter<P> getParameter() {
        return parameter;
    }

    public H getHandler() {
        return handler;
    }

    public void setParameter(Parameter<P> parameter) {
        this.parameter = parameter;
    }

    public void setHandler(H handler) {
        this.handler = handler;
    }

    public void check(Consumer<Component> errorConsumer) {
        //converters.check(errorConsumer, parameter.type(), supplier.getValueType());
    }
}

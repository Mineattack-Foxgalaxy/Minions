package io.github.skippyall.minions.gui.instruction;

import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.handler.ConfiguredValueHandler;
import io.github.skippyall.minions.program.handler.Parameter;
import io.github.skippyall.minions.program.handler.ValueHandlerList;
import io.github.skippyall.minions.program.handler.ValueHandlerType;
import io.github.skippyall.minions.program.handler.consumer.ValueConsumer;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;

public class ValueConsumerGui extends ValueHandlerGui<ValueConsumer> {
    public ValueConsumerGui(MinionsGui parent, ConfiguredInstruction instruction, Parameter<?> parameter, Context resolutionContext) {
        super(parent, instruction, parameter, resolutionContext);
    }

    @Override
    protected ValueHandlerList<ValueConsumer, ? extends ConfiguredValueHandler<?, ValueConsumer>> getHandlerList() {
        return instruction.getValueConsumers();
    }

    @Override
    protected Registry<ValueHandlerType<ValueConsumer>> getTypeRegistry() {
        return MinionRegistries.VALUE_CONSUMER_TYPES;
    }

    @Override
    protected void configureConvertersMenu() {
        if(entry != null) {
            Result<ValueType<?>, Component> result = entry.getHandler().getCurrentAcceptedType(resolutionContext);
            if(result instanceof Result.Success<ValueType<?>, Component> success) {
                new ConverterListGui(this, entry.getConverters(), entry.getParameter().type(), success.result());
            }
        }
    }
}

package io.github.skippyall.minions.gui.instruction;

import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.handler.ConfiguredValueHandler;
import io.github.skippyall.minions.program.handler.Parameter;
import io.github.skippyall.minions.program.handler.ValueHandlerList;
import io.github.skippyall.minions.program.handler.ValueHandlerType;
import io.github.skippyall.minions.program.handler.supplier.ValueSupplier;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.registration.MinionRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;

public class ValueSupplierGui extends ValueHandlerGui<ValueSupplier> {
    public ValueSupplierGui(MinionsGui parent, ConfiguredInstruction instruction, Parameter<?> parameter, Context resolutionContext) {
        super(parent, instruction, parameter, resolutionContext);
    }

    @Override
    protected ValueHandlerList<ValueSupplier, ? extends ConfiguredValueHandler<?, ValueSupplier>> getHandlerList() {
        return instruction.getArguments();
    }

    @Override
    protected Registry<ValueHandlerType<ValueSupplier>> getTypeRegistry() {
        return MinionRegistries.VALUE_SUPPLIER_TYPES;
    }

    @Override
    protected void configureConvertersMenu() {
        if(entry != null) {
            Result<TypedValue<?>, Component> result = entry.getHandler().resolve(resolutionContext);
            if(result instanceof Result.Success<TypedValue<?>, Component> success) {
                new ConverterListGui(this, entry.getConverters(), success.result().type(), entry.getParameter().type());
            }
        }
    }
}

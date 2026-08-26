package io.github.skippyall.minions.block.miniontrigger;

import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.handler.ValueHandlerType;
import io.github.skippyall.minions.program.handler.supplier.ValueSupplier;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.registration.ResolutionContext;
import io.github.skippyall.minions.registration.ValueSuppliers;
import net.minecraft.network.chat.Component;

public class ConnectedBlockSupplier implements ValueSupplier {
    public static final ConnectedBlockSupplier INSTANCE = new ConnectedBlockSupplier();

    @Override
    public Result<TypedValue<?>, Component> resolve(Context context) {
        MinionTriggerBlockEntity be = context.getOrThrow(ResolutionContext.MINION_TRIGGER);
        String paramName = context.getOrThrow(ResolutionContext.PARAMETER_NAME);

        TypedValue<?> value = be.withConnectedBlockCache(cache -> cache.getConnectedBlockValue(paramName));

        return Result.ofNullable(value, Component.translatable("value_supplier.minions.connected_block.no_block"));
    }

    @Override
    public ValueHandlerType<ValueSupplier> getType() {
        return ValueSuppliers.CONNECTED_BLOCK_SUPPLIER_TYPE;
    }

    @Override
    public Component getDisplayText() {
        return Component.translatable("value_supplier.minions.connected_block");
    }
}

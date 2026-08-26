package io.github.skippyall.minions.block.miniontrigger;

import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.handler.ValueHandlerType;
import io.github.skippyall.minions.program.handler.consumer.ValueConsumer;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.ResolutionContext;
import io.github.skippyall.minions.registration.ValueConsumers;
import net.minecraft.network.chat.Component;

public class ConnectedBlockConsumer implements ValueConsumer {
    public static final ConnectedBlockConsumer INSTANCE = new ConnectedBlockConsumer();

    @Override
    public void consume(TypedValue<?> value, Context context) {
        MinionTriggerBlockEntity be = context.getOrThrow(ResolutionContext.MINION_TRIGGER);
        String paramName = context.getOrThrow(ResolutionContext.PARAMETER_NAME);

        be.executeWithConnectedBlockCache(cache -> cache.setConnectedBlockValue(paramName, value));
    }

    @Override
    public Result<ValueType<?>, Component> getCurrentAcceptedType(Context context) {
        MinionTriggerBlockEntity be = context.getOrThrow(ResolutionContext.MINION_TRIGGER);
        String paramName = context.getOrThrow(ResolutionContext.PARAMETER_NAME);

        ValueType<?> blockType = be.withConnectedBlockCache(cache -> cache.getConnectedBlockType(paramName));

        return Result.ofNullable(blockType, Component.translatable("value_supplier.minions.connected_block.no_block"));
    }

    @Override
    public ValueHandlerType<ValueConsumer> getType() {
        return ValueConsumers.CONNECTED_BLOCK_CONSUMER_TYPE;
    }

    @Override
    public Component getDisplayText() {
        return Component.translatable("value_supplier.minions.connected_block");
    }
}

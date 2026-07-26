package io.github.skippyall.minions.program.handler.consumer;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.handler.ValueHandler;
import io.github.skippyall.minions.program.handler.ValueHandlerType;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;
import net.minecraft.network.chat.Component;

/**
 * A <code>ValueConsumer</code> can be supplied to an instruction with a matching return parameter.
 * When the execution returns a value for that parameter, it will call the <code>consume</code> method.
 */
public interface ValueConsumer extends ValueHandler<ValueConsumer> {
    Codec<ValueConsumer> CODEC = MinionRegistries.VALUE_CONSUMER_TYPES.byNameCodec().dispatch(
            "type",
            ValueConsumer::getType,
            type -> type.getCodec().fieldOf("data")
    );

    void consume(TypedValue<?> value, Context context);

    Result<ValueType<?>, Component> getCurrentAcceptedType(Context context);

    ValueHandlerType<ValueConsumer> getType();

    Component getDisplayText();
}

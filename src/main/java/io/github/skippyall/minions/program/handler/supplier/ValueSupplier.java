package io.github.skippyall.minions.program.handler.supplier;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.handler.ValueHandler;
import io.github.skippyall.minions.program.handler.ValueHandlerType;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.registration.MinionRegistries;
import net.minecraft.network.chat.Component;

/**
 * An <code>ValueSupplier</code> can be supplied to an instruction with a matching parameter.
 * Its value is resolved at runtime and can vary between executions.
 */
public interface ValueSupplier extends ValueHandler<ValueSupplier> {
    Codec<ValueSupplier> CODEC = MinionRegistries.VALUE_SUPPLIER_TYPES.byNameCodec().dispatch(
            "type",
            ValueSupplier::getType,
            type -> type.getCodec().fieldOf("data")
    );

    Result<TypedValue<?>, Component> resolve(Context context);

    ValueHandlerType<ValueSupplier> getType();

    Component getDisplayText();
}

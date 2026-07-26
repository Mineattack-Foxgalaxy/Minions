package io.github.skippyall.minions.program.handler.supplier;

import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.value.TypedValue;
import net.minecraft.network.chat.Component;

/**
 * A supplier that always resolves to a fixed value
 */
public class FixedValueSupplier implements ValueSupplier {
    private final FixedValueSupplierType type;
    private final TypedValue<?> value;

    public FixedValueSupplier(FixedValueSupplierType type, TypedValue<?> value) {
        this.type = type;
        this.value = value;
    }

    public TypedValue<?> getValue() {
        return value;
    }

    @Override
    public Result<TypedValue<?>, Component> resolve(Context context) {
        return new Result.Success<>(value);
    }

    @Override
    public FixedValueSupplierType getType() {
        return type;
    }

    @Override
    public Component getDisplayText() {
        return value.getDisplayText();
    }
}

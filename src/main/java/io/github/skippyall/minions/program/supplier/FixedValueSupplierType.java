package io.github.skippyall.minions.program.supplier;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.program.value.ValueType;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class FixedValueSupplierType extends ValueSupplierType {
    @Override
    public Codec<FixedValueSupplier> getCodec() {
        return TypedValue.CODEC.xmap(v -> new FixedValueSupplier(this, v), FixedValueSupplier::getValue);
    }

    @Override
    public <V> CompletableFuture<FixedValueSupplier> openConfiguration(MinionsGui parent, ValueType<V> valueType, @Nullable ValueSupplier previousValueSupplier) {
        V value = null;
        if(previousValueSupplier instanceof FixedValueSupplier val) {
            value = valueType.checkedCast(val.getValue());
        }
        if(value == null) {
            value = valueType.defaultValue();
        }

        return valueType.openValueDialog(parent, value)
                .thenApply(newValue -> new FixedValueSupplier(this, new TypedValue<>(newValue, valueType)));
    }
}

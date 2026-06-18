package io.github.skippyall.minions.program.supplier;

import io.github.skippyall.minions.program.value.ValueType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

/**
 * A supplier that always resolves to a fixed value
 */
public class FixedValueSupplier<T> implements ValueSupplier<T> {
    private final FixedValueSupplierType type;
    private final ValueType<T> valueType;
    private final T value;

    public FixedValueSupplier(FixedValueSupplierType type, ValueType<T> valueType, T value) {
        this.type = type;
        this.valueType = valueType;
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public T resolve(MinecraftServer server) {
        return value;
    }

    @Override
    public ValueType<T> getValueType() {
        return valueType;
    }

    @Override
    public FixedValueSupplierType getType() {
        return type;
    }

    @Override
    public Component getDisplayText() {
        return valueType.getDisplayText(value);
    }
}

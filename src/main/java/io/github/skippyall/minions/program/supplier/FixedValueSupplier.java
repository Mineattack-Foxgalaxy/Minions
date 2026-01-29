package io.github.skippyall.minions.program.supplier;

import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.value.ValueType;
import net.minecraft.text.Text;

/**
 * A supplier that always resolves to a fixed value
 */
public class FixedValueSupplier<T, R extends InstructionRuntime<R>> implements ValueSupplier<T, R> {
    private final FixedValueSupplierType<R> type;
    private final ValueType<T> valueType;
    private final T value;

    public FixedValueSupplier(FixedValueSupplierType<R> type, ValueType<T> valueType, T value) {
        this.type = type;
        this.valueType = valueType;
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public T resolve(R runtime) {
        return value;
    }

    @Override
    public ValueType<T> getValueType() {
        return valueType;
    }

    @Override
    public FixedValueSupplierType<R> getType() {
        return type;
    }

    @Override
    public Text getDisplayText() {
        return Text.translatable("value_supplier_type.minions.fixed.display", valueType.getDisplayText(value));
    }
}

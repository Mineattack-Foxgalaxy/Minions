package io.github.skippyall.minions.program.supplier;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.value.ValueType;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class FixedValueSupplierType<R extends InstructionRuntime<R>> extends ValueSupplierType<R> {
    @Override
    public <T> Codec<FixedValueSupplier<T,R>> getCodec(ValueType<T> valueType) {
        return valueType.codec().xmap(value -> new FixedValueSupplier<>(this, valueType, value), FixedValueSupplier::getValue);
    }

    @Override
    public <V> CompletableFuture<FixedValueSupplier<?,R>> openConfiguration(MinionsGui parent, ValueType<V> valueType, @Nullable ValueSupplier<?,R> previousValueSupplier) {
        return valueType.openValueDialog(
                parent,
                previousValueSupplier instanceof FixedValueSupplier<?,R> val && val.getValueType() == valueType ? valueType.checkedCast(val.getValue()) : valueType.defaultValue()
        ).thenApply(value -> new FixedValueSupplier<>(this, valueType, value));
    }
}

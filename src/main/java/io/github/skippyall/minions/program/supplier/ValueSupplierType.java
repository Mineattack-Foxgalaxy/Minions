package io.github.skippyall.minions.program.supplier;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.value.ValueType;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class ValueSupplierType {
    public abstract <T> @Nullable Codec<? extends ValueSupplier<T>> getCodec(ValueType<T> type);

    public abstract <T> CompletableFuture<? extends ValueSupplier<?>> openConfiguration(MinionsGui gui, ValueType<T> valueType, @Nullable ValueSupplier<?> previous);
}

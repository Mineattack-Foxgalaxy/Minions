package io.github.skippyall.minions.program.supplier;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.value.ValueType;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class ValueSupplierType<R extends InstructionRuntime<R>> {
    public abstract <T> Codec<? extends ValueSupplier<T,R>> getCodec(ValueType<T> type);

    public abstract <T> CompletableFuture<? extends ValueSupplier<?,R>> openConfiguration(MinionsGui gui, ValueType<T> valueType, @Nullable ValueSupplier<?,R> previous);
}

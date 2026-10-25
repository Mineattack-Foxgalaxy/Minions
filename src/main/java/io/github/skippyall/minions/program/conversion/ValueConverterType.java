package io.github.skippyall.minions.program.conversion;

import com.mojang.serialization.MapCodec;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.program.value.ValueType;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface ValueConverterType<C extends ValueConverter> {
    MapCodec<C> getCodec();

    boolean isSupportedConversion(ValueType<?> from);

    <F> CompletableFuture<? extends C> configure(MinionsGui parent, ValueType<F> from, @Nullable ValueConverter old);
}

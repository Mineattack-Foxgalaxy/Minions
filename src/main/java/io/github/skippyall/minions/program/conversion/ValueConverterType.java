package io.github.skippyall.minions.program.conversion;

import com.mojang.serialization.MapCodec;
import io.github.skippyall.minions.program.value.ValueType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface ValueConverterType<C extends ValueConverter<?,?>> {
    MapCodec<C> getCodec();

    boolean isSupportedConversion(ValueType<?> from, ValueType<?> to);

    <F,T> CompletableFuture<C> configure(ServerPlayerEntity player, ValueType<F> from, ValueType<T> to, @Nullable C old);
}

package io.github.skippyall.minions.program.value.conversion;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.value.ValueType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface ValueConverterType<C extends ValueConverter<?,?>> {
    Codec<C> getCodec(ValueType<?> from, ValueType<?> to);

    boolean isSupportedFromType(ValueType<?> type);

    boolean isSupportedToType(ValueType<?> type);

    CompletableFuture<C> configure(ServerPlayerEntity player, ValueType<?> from, ValueType<?> to, @Nullable C old);
}

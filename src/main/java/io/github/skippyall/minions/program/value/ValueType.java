package io.github.skippyall.minions.program.value;

import com.mojang.serialization.Codec;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public record ValueType<T>(Codec<T> codec, T defaultValue, BiFunction<ServerPlayerEntity, T, CompletableFuture<T>> valueDialogOpener) {
    public CompletableFuture<T> openValueDialog(ServerPlayerEntity player, T previousValue) {
        return valueDialogOpener.apply(player, previousValue);
    }
}

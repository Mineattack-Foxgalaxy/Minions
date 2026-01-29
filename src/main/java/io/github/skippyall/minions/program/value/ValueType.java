package io.github.skippyall.minions.program.value;

import com.mojang.serialization.Codec;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public record ValueType<T>(Codec<T> codec, T defaultValue, BiFunction<ServerPlayerEntity, T, CompletableFuture<T>> valueDialogOpener, Function<T, Text> textDisplay) {
    public CompletableFuture<T> openValueDialog(ServerPlayerEntity player, T previousValue) {
        return valueDialogOpener.apply(player, previousValue);
    }

    public Text getDisplayText(T value) {
        return textDisplay.apply(value);
    }
}

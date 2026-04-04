package io.github.skippyall.minions.program.value;

import com.mojang.serialization.Codec;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface ValueType<T> {
    CompletableFuture<T> openValueDialog(ServerPlayerEntity player, T previousValue);

    Text getDisplayText(T value);

    Codec<T> codec();

    T defaultValue();

    @Nullable T checkedCast(Object value);

    default boolean isOf(Object value) {
        return checkedCast(value) != null;
    }
}

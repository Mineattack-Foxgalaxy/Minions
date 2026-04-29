package io.github.skippyall.minions.program.value;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.gui.MinionsGui;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface ValueType<T> {
    CompletableFuture<T> openValueDialog(MinionsGui gui, T previousValue);

    Component getDisplayText(T value);

    Codec<T> codec();

    T defaultValue();

    @Nullable T checkedCast(Object value);

    default boolean isOf(Object value) {
        return checkedCast(value) != null;
    }
}

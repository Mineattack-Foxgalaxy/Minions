package io.github.skippyall.minions.program.value;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.gui.MinionsGui;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public record SimpleValueType<T>(Codec<T> codec, T defaultValue, Function<Object, @Nullable T> checkedCast, BiFunction<MinionsGui, T, CompletableFuture<@Nullable T>> valueDialogOpener, Function<T, Component> textDisplay) implements ValueType<T> {
    @Override
    public CompletableFuture<@Nullable T> openValueDialog(MinionsGui parent, @Nullable T previousValue) {
        if(previousValue == null) {
            previousValue = defaultValue;
        }
        return valueDialogOpener.apply(parent, previousValue);
    }

    @Override
    public Component getDisplayText(T value) {
        return textDisplay.apply(value);
    }

    @Override
    public @Nullable T checkedCast(Object value) {
        return checkedCast.apply(value);
    }
}

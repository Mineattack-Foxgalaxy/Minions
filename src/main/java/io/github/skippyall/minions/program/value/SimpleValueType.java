package io.github.skippyall.minions.program.value;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.gui.MinionsGui;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.network.chat.Component;

public record SimpleValueType<T>(Codec<T> codec, T defaultValue, Function<Object, T> checkedCast, BiFunction<MinionsGui, T, CompletableFuture<T>> valueDialogOpener, Function<T, Component> textDisplay) implements ValueType<T> {
    @Override
    public CompletableFuture<T> openValueDialog(MinionsGui parent, T previousValue) {
        return valueDialogOpener.apply(parent, previousValue);
    }

    @Override
    public Component getDisplayText(T value) {
        return textDisplay.apply(value);
    }

    @Override
    public T checkedCast(Object value) {
        return checkedCast.apply(value);
    }
}

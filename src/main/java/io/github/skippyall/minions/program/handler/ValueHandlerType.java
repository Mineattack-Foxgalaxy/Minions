package io.github.skippyall.minions.program.handler;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.program.value.ValueType;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class ValueHandlerType<H> {
    public abstract Codec<? extends H> getCodec();

    public abstract <T> CompletableFuture<? extends H> openConfiguration(MinionsGui gui, ValueType<T> valueType, @Nullable H previous);

    public interface Singleton<H> {
        H getHandler();
    }
}

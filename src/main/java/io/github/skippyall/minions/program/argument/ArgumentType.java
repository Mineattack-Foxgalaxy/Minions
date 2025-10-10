package io.github.skippyall.minions.program.argument;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.value.ValueType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class ArgumentType<R extends InstructionRuntime<R>> {
    public abstract <T> Codec<? extends Argument<T,R>> getCodec(ValueType<T> type);

    public abstract <T> CompletableFuture<? extends Argument<T,R>> openConfiguration(ServerPlayerEntity player, ValueType<T> valueType, @Nullable Argument<T,R> previous);
}

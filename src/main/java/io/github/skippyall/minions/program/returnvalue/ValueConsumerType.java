package io.github.skippyall.minions.program.returnvalue;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.argument.Argument;
import io.github.skippyall.minions.program.value.ValueType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class ValueConsumerType<R extends InstructionRuntime<R>> {
    public abstract <T> Codec<? extends ValueConsumer<T,R>> getCodec(ValueType<T> type);

    public abstract <T> CompletableFuture<? extends ValueConsumer<T,R>> openConfiguration(ServerPlayerEntity player, ValueType<T> valueType, @Nullable ValueConsumer<T,R> previous);
}

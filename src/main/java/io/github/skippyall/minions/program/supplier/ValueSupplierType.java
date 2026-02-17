package io.github.skippyall.minions.program.supplier;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.value.ValueType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface ValueSupplierType<R extends InstructionRuntime<R>> {
    <T> Codec<? extends ValueSupplier<T,R>> getCodec(ValueType<T> type);

    default boolean isConfigurable(ServerPlayerEntity player, ValueType<?> valueType, MinionFakePlayer minion) {
        return true;
    }

    <T> CompletableFuture<? extends ValueSupplier<T,R>> openConfiguration(ServerPlayerEntity player, ValueType<T> valueType, @Nullable ValueSupplier<T,R> previous);
}

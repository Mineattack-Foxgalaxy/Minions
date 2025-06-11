package io.github.skippyall.minions.new_program.value;

import com.mojang.serialization.Codec;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.concurrent.CompletableFuture;

public interface ValueType<T> {
    Codec<T> getCodec();

    CompletableFuture<T> openValueDialog(ServerPlayerEntity player, T previousValue);
}

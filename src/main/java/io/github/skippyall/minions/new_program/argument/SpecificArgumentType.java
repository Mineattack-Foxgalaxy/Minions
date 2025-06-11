package io.github.skippyall.minions.new_program.argument;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.new_program.value.ValueType;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.concurrent.CompletableFuture;

public abstract class SpecificArgumentType<V, A extends Argument<V>> {
    protected final ValueType<V> valueType;

    public SpecificArgumentType(ValueType<V> valueType) {
        this.valueType = valueType;
    }

    public abstract Codec<A> getArgumentCodec();

    public abstract CompletableFuture<A> openArgumentDialog(ServerPlayerEntity player, A previousArgument);

    public ValueType<V> getValueType() {
        return valueType;
    }
}

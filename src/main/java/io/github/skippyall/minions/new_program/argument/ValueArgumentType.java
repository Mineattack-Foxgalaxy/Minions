package io.github.skippyall.minions.new_program.argument;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.new_program.value.ValueType;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.concurrent.CompletableFuture;

public class ValueArgumentType<V> extends SpecificArgumentType<V, ValueArgument<V>> {
    public ValueArgumentType(ValueType<V> valueType) {
        super(valueType);
    }

    @Override
    public Codec<ValueArgument<V>> getArgumentCodec() {
        return valueType.getCodec().xmap(value -> new ValueArgument<>(valueType, value), ValueArgument::getValue);
    }

    @Override
    public CompletableFuture<ValueArgument<V>> openArgumentDialog(ServerPlayerEntity player, ValueArgument<V> previousArgument) {
        return valueType.openValueDialog(player, previousArgument.getValue()).thenApply(value -> new ValueArgument<>(valueType, value));
    }
}

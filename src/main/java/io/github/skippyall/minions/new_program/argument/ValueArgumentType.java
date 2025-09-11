package io.github.skippyall.minions.new_program.argument;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.new_program.value.ValueType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ValueArgumentType<V> extends SpecificArgumentType<V, ValueArgument<V>> {
    public ValueArgumentType(ValueType<V> valueType) {
        super(valueType);
    }

    @Override
    public GenericArgumentType getGenericArgumentType() {
        return Arguments.VALUE_ARGUMENT;
    }

    @Override
    public Codec<ValueArgument<V>> getArgumentCodec() {
        return valueType.codec().xmap(value -> new ValueArgument<>(this, value), ValueArgument::getValue);
    }

    @Override
    public CompletableFuture<ValueArgument<V>> openArgumentDialog(ServerPlayerEntity player, @Nullable ValueArgument<V> previousArgument) {
        return valueType.openValueDialog(
                player,
                previousArgument != null ? previousArgument.getValue() : valueType.defaultValue()
        ).thenApply(value -> new ValueArgument<>(this, value));
    }
}

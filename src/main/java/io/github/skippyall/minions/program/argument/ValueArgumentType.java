package io.github.skippyall.minions.program.argument;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.value.ValueType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ValueArgumentType<V, R extends InstructionRuntime<R>> extends SpecificArgumentType<V, ValueArgument<V,R>, R> {
    public ValueArgumentType(ValueType<V> valueType, GenericArgumentType<R> genericType) {
        super(valueType, genericType);
    }

    @Override
    public Codec<ValueArgument<V,R>> getArgumentCodec() {
        return valueType.codec().xmap(value -> new ValueArgument<>(this, value), ValueArgument::getValue);
    }

    @Override
    public CompletableFuture<ValueArgument<V,R>> openArgumentDialog(ServerPlayerEntity player, @Nullable ValueArgument<V,R> previousArgument) {
        return valueType.openValueDialog(
                player,
                previousArgument != null ? previousArgument.getValue() : valueType.defaultValue()
        ).thenApply(value -> new ValueArgument<>(this, value));
    }
}

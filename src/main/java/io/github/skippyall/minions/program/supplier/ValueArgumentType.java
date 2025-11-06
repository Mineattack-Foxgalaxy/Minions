package io.github.skippyall.minions.program.supplier;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.value.ValueType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ValueArgumentType<R extends InstructionRuntime<R>> extends ValueSupplierType<R> {
    @Override
    public <T> Codec<ValueArgument<T,R>> getCodec(ValueType<T> valueType) {
        return valueType.codec().xmap(value -> new ValueArgument<>(this, valueType, value), ValueArgument::getValue);
    }

    @Override
    public <V> CompletableFuture<ValueArgument<V,R>> openConfiguration(ServerPlayerEntity player, ValueType<V> valueType, @Nullable ValueSupplier<V,R> previousValueSupplier) {
        return valueType.openValueDialog(
                player,
                previousValueSupplier instanceof ValueArgument<V,R> val ? val.getValue() : valueType.defaultValue()
        ).thenApply(value -> new ValueArgument<>(this, valueType, value));
    }
}

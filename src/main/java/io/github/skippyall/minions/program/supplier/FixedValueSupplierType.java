package io.github.skippyall.minions.program.supplier;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.value.ValueType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class FixedValueSupplierType<R extends InstructionRuntime<R>> implements ValueSupplierType<R> {
    @Override
    public <T> Codec<FixedValueSupplier<T,R>> getCodec(ValueType<T> valueType) {
        return valueType.codec().xmap(value -> new FixedValueSupplier<>(this, valueType, value), FixedValueSupplier::getValue);
    }

    @Override
    public <V> CompletableFuture<FixedValueSupplier<V,R>> openConfiguration(ServerPlayerEntity player, ValueType<V> valueType, @Nullable ValueSupplier<V,R> previousValueSupplier) {
        return valueType.openValueDialog(
                player,
                previousValueSupplier instanceof FixedValueSupplier<V,R> val ? val.getValue() : valueType.defaultValue()
        ).thenApply(value -> new FixedValueSupplier<>(this, valueType, value));
    }
}

package io.github.skippyall.minions.program.handler.supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.program.handler.ValueHandlerType;
import io.github.skippyall.minions.program.value.ValueType;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class ValueSupplierType extends ValueHandlerType<ValueSupplier> {
    public abstract Codec<? extends ValueSupplier> getCodec();

    public abstract <T> CompletableFuture<? extends ValueSupplier> openConfiguration(MinionsGui gui, ValueType<T> valueType, @Nullable ValueSupplier previous);

    public static class Singleton extends ValueSupplierType implements ValueHandlerType.Singleton<ValueSupplier> {
        private final ValueSupplier supplier;

        public Singleton(ValueSupplier supplier) {
            this.supplier = supplier;
        }

        @Override
        public ValueSupplier getHandler() {
            return supplier;
        }

        public ValueSupplier getSupplier() {
            return supplier;
        }

        @Override
        public Codec<? extends ValueSupplier> getCodec() {
            return MapCodec.unitCodec(supplier);
        }

        @Override
        public <T> CompletableFuture<? extends ValueSupplier> openConfiguration(MinionsGui gui, ValueType<T> valueType, @Nullable ValueSupplier previous) {
            return CompletableFuture.completedFuture(supplier);
        }
    }
}

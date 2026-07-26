package io.github.skippyall.minions.program.handler.supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.program.handler.ValueHandlerType;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.util.TranslationUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class ValueSupplierType extends ValueHandlerType<ValueSupplier> {
    public abstract Codec<? extends ValueSupplier> getCodec();

    public abstract <T> CompletableFuture<? extends ValueSupplier> openConfiguration(MinionsGui gui, ValueType<T> valueType, @Nullable ValueSupplier previous);

    @Override
    public String getTranslationKey() {
        return TranslationUtil.getTranslationKey(
                this,
                MinionRegistries.VALUE_SUPPLIER_TYPES,
                "minions.gui.not_set"
        );
    }

    @Override
    public ItemStack getDisplayStack(RegistryAccess access) {
        return GuiDisplay.getDisplayStack(MinionRegistries.VALUE_SUPPLIER_TYPES, this, access);
    }

    public static class Singleton extends ValueSupplierType {
        private final ValueSupplier supplier;

        public Singleton(ValueSupplier supplier) {
            this.supplier = supplier;
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

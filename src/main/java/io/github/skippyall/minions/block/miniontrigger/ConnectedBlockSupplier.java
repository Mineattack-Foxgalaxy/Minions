package io.github.skippyall.minions.block.miniontrigger;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.supplier.ValueSupplier;
import io.github.skippyall.minions.program.supplier.ValueSupplierType;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.ResolutionContext;
import io.github.skippyall.minions.registration.ValueSuppliers;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ConnectedBlockSupplier implements ValueSupplier {
    public static final ConnectedBlockSupplier INSTANCE = new ConnectedBlockSupplier();

    @Override
    public Result<TypedValue<?>, Component> resolve(Context context) {
        MinionTriggerBlockEntity be = context.getOrThrow(ResolutionContext.MINION_TRIGGER);
        String paramName = context.getOrThrow(ResolutionContext.PARAMETER_NAME);

        return Result.ofNullable(be.getValue(paramName), Component.literal("What?"));
    }

    @Override
    public ValueSupplierType getType() {
        return ValueSuppliers.CONNECTED_BLOCK_SUPPLIER_TYPE;
    }

    @Override
    public Component getDisplayText() {
        return Component.translatable("value_supplier.minions.connected_block");
    }

    public static class ConnectedBlockSupplierType extends ValueSupplierType {
        @Override
        public Codec<? extends ValueSupplier> getCodec() {
            return MapCodec.unitCodec(INSTANCE);
        }

        @Override
        public <T> CompletableFuture<? extends ValueSupplier> openConfiguration(MinionsGui gui, ValueType<T> valueType, @Nullable ValueSupplier previous) {
            return CompletableFuture.completedFuture(INSTANCE);
        }
    }
}

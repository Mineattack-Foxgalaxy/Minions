package io.github.skippyall.minions.program.conversion;

import com.mojang.serialization.MapCodec;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.registration.ValueTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EqualityConverter<F> implements ValueConverter<F, Boolean> {
    public static final MapCodec<EqualityConverter<?>> CODEC = MinionRegistries.VALUE_TYPES.getCodec().dispatchMap(
            EqualityConverter::getFrom,
            EqualityConverter::getCodec
    );

    private ValueType<F> fromType;
    private F compareValue;

    public EqualityConverter(ValueType<F> fromType, F compareValue) {
        this.fromType = fromType;
        this.compareValue = compareValue;
    }

    @Override
    public Boolean convert(F from) {
        return compareValue.equals(from);
    }

    @Override
    public ValueType<F> getFrom() {
        return fromType;
    }

    @Override
    public ValueType<Boolean> getTo() {
        return ValueTypes.BOOLEAN;
    }

    @Override
    public ValueConverterType<?> getType() {
        return null;
    }

    private static <F> MapCodec<EqualityConverter<F>> getCodec(ValueType<F> fromType) {
        return fromType.codec().fieldOf("compareValue")
                .xmap(compareValue -> new EqualityConverter<>(fromType, compareValue), converter -> converter.compareValue);
    }

    public static class EqualityConverterType implements ValueConverterType<EqualityConverter<?>> {
        @Override
        public MapCodec<EqualityConverter<?>> getCodec() {
            return CODEC;
        }

        @Override
        public boolean isSupportedConversion(ValueType<?> from, ValueType<?> to) {
            return to == ValueTypes.BOOLEAN;
        }

        @Override
        public <F,T> CompletableFuture<EqualityConverter<?>> configure(ServerPlayerEntity player, ValueType<F> from, ValueType<T> to, @Nullable EqualityConverter<?> old) {
            if(to == ValueTypes.BOOLEAN) {
                //noinspection unchecked
                return from.openValueDialog(player, old != null && old.fromType == from ? (F) old.compareValue : null)
                        .thenApply(compareValue -> new EqualityConverter<>(from, compareValue));
            } else {
                return CompletableFuture.failedFuture(new IllegalArgumentException("EqualityConverter does not support converting to " + to));
            }
        }
    }
}

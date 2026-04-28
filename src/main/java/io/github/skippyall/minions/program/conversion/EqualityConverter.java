package io.github.skippyall.minions.program.conversion;

import com.mojang.serialization.MapCodec;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.registration.ValueConverters;
import io.github.skippyall.minions.registration.ValueTypes;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EqualityConverter<F> implements ValueConverter<F, Boolean> {
    public static final MapCodec<EqualityConverter<?>> CODEC = MinionRegistries.VALUE_TYPES.getCodec().dispatchMap(
            "value_type",
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
    public Result<Boolean, Text> convert(F from) {
        return new Result.Success<>(compareValue.equals(from));
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
        return ValueConverters.EQUALITY_CONVERTER;
    }

    @Override
    public Text getDisplayText() {
        return Text.translatable("value_converter.minions.equality.display", fromType.getDisplayText(compareValue));
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
        public <F,T> CompletableFuture<EqualityConverter<?>> configure(MinionsGui parent, ValueType<F> from, ValueType<T> to, @Nullable ValueConverter<?,?> old) {
            F oldValue = null;
            if(old instanceof EqualityConverter<?> eq && eq.fromType == from) {
                oldValue = from.checkedCast(eq.compareValue);
            }
            return from.openValueDialog(parent, oldValue)
                    .thenApply(compareValue -> new EqualityConverter<>(from, compareValue));
        }
    }
}

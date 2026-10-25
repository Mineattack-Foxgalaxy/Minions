package io.github.skippyall.minions.program.conversion;

import com.mojang.serialization.MapCodec;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.registration.ValueTypes;
import io.github.skippyall.minions.util.FutureUtil;
import io.github.skippyall.minions.util.TranslationUtil;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.DoubleBinaryOperator;
import java.util.function.LongBinaryOperator;

public class ArithmeticConverter implements ValueConverter {
    private final TypedValue<?> addValue;
    private final Type converterType;

    public ArithmeticConverter(TypedValue<?> addValue, Type converterType) {
        this.addValue = addValue;
        this.converterType = converterType;
    }

    @Override
    public Result<? extends TypedValue<?>, Component> convert(TypedValue<?> from) {
        TypedValue<Long> fromLong = from.checkedCast(ValueTypes.LONG);
        TypedValue<Long> addLong = addValue.checkedCast(ValueTypes.LONG);

        Result<Double, Component> result = Casts.cast(from, ValueTypes.DOUBLE);

        if(fromLong != null && addLong != null) {
            return new Result.Success<>(new TypedValue<>(converterType.longOperation.applyAsLong(fromLong.value(), addLong.value()), ValueTypes.LONG));
        } else switch(result) {
            case Result.Success<Double, Component> (Double fromDouble) -> {
                double add = Casts.cast(addValue, ValueTypes.DOUBLE).getOrThrow();
                return new Result.Success<>(new TypedValue<>(converterType.doubleOperation.applyAsDouble(fromDouble, add), ValueTypes.DOUBLE));
            }
            case Result.Error<Double, Component> error -> {
                return error.forResultType();
            }
        }
    }

    @Override
    public ValueType<?> getOutputType(ValueType<?> inputType) {
        if(inputType == ValueTypes.LONG && addValue.type() == ValueTypes.LONG) {
            return ValueTypes.LONG;
        } else {
            return ValueTypes.DOUBLE;
        }
    }

    @Override
    public ValueConverterType<?> getType() {
        return converterType;
    }

    @Override
    public Component getDisplayText() {
        return Component.translatable(TranslationUtil.getTranslationKey(converterType, MinionRegistries.VALUE_CONVERTER_TYPES) + ".display", addValue.getDisplayText());
    }

    public static class Type implements ValueConverterType<ArithmeticConverter> {
        private final LongBinaryOperator longOperation;
        private final DoubleBinaryOperator doubleOperation;

        public Type(LongBinaryOperator longOperation, DoubleBinaryOperator doubleOperation) {
            this.longOperation = longOperation;
            this.doubleOperation = doubleOperation;
        }

        @Override
        public MapCodec<ArithmeticConverter> getCodec() {
            return TypedValue.CODEC.xmap((value) -> new ArithmeticConverter(value, this), c -> c.addValue).fieldOf("compareValue");
        }

        @Override
        public boolean isSupportedConversion(ValueType<?> from) {
            return (from == ValueTypes.LONG || from == ValueTypes.DOUBLE);
        }

        @Override
        public <F> CompletableFuture<? extends ArithmeticConverter> configure(MinionsGui parent, ValueType<F> from, @Nullable ValueConverter old) {
            Double oldValue = null;
            if(old instanceof ArithmeticConverter ac) {
                oldValue = Casts.cast(ac.addValue, ValueTypes.DOUBLE).getOptional().orElse(null);
            }
            return ValueTypes.DOUBLE.openValueDialog(parent, oldValue)
                    .thenCompose(FutureUtil::failIfNull)
                    .thenApply(compareValue -> {
                        if(compareValue % 1 == 0) {
                            return new ArithmeticConverter(new TypedValue<>(compareValue.longValue(), ValueTypes.LONG), this);
                        } else {
                            return new ArithmeticConverter(new TypedValue<>(compareValue, ValueTypes.DOUBLE), this);
                        }
                    });
        }
    }
}

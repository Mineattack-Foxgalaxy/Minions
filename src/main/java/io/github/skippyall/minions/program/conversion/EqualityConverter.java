package io.github.skippyall.minions.program.conversion;

import com.mojang.serialization.MapCodec;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.ValueConverters;
import io.github.skippyall.minions.registration.ValueTypes;
import io.github.skippyall.minions.util.FutureUtil;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EqualityConverter implements ValueConverter {
    public static final MapCodec<EqualityConverter> CODEC = TypedValue.CODEC.xmap(EqualityConverter::new, converter -> converter.compareValue).fieldOf("compareValue");

    TypedValue<?> compareValue;

    public EqualityConverter(TypedValue<?> compareValue) {
        this.compareValue = compareValue;
    }

    @Override
    public Result<? extends TypedValue<?>, Component> convert(TypedValue<?> from) {
        boolean equal = from.value().equals(compareValue.value());
        return new Result.Success<>(new TypedValue<>(equal, ValueTypes.BOOLEAN));
    }

    @Override
    public ValueType<?> getOutputType(ValueType<?> inputType) {
        return ValueTypes.BOOLEAN;
    }

    @Override
    public ValueConverterType<?> getType() {
        return ValueConverters.EQUALITY_CONVERTER;
    }

    @Override
    public Component getDisplayText() {
        return Component.translatable("value_converter.minions.equality.display", compareValue.getDisplayText());
    }

    public static class Type implements ValueConverterType<EqualityConverter> {
        @Override
        public MapCodec<EqualityConverter> getCodec() {
            return CODEC;
        }

        @Override
        public boolean isSupportedConversion(ValueType<?> from) {
            return true;
        }

        @Override
        public <F> CompletableFuture<EqualityConverter> configure(MinionsGui parent, ValueType<F> from, @Nullable ValueConverter old) {
            F oldValue = null;
            if(old instanceof EqualityConverter eq) {
                oldValue = from.checkedCast(eq.compareValue);
            }
            return from.openValueDialog(parent, oldValue)
                    .thenCompose(FutureUtil::failIfNull)
                    .thenApply(compareValue -> new EqualityConverter(new TypedValue<>(compareValue, from)));
        }
    }
}

package io.github.skippyall.minions.program.conversion;

import com.mojang.serialization.Codec;
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

public class DivideConverter implements ValueConverter {
    private final double divident;

    public DivideConverter(double divident) {
        this.divident = divident;
    }

    @Override
    public Result<? extends TypedValue<?>, Component> convert(TypedValue<?> from) {
        Result<Double, Component> result = Casts.cast(from, ValueTypes.DOUBLE);

        return switch(result) {
            case Result.Success<Double, Component> (Double divisor) -> {
                if(divisor == 0) {
                    yield new Result.Error<>(Component.translatable("value_converter.minions.divide.divide_by_zero"));
                } else {
                    yield new Result.Success<>(new TypedValue<>(divident / divisor, ValueTypes.DOUBLE));
                }
            }
            case Result.Error<Double, Component> error -> error.forResultType();
        };
    }

    @Override
    public ValueType<?> getOutputType(ValueType<?> inputType) {
        return ValueTypes.DOUBLE;
    }

    @Override
    public ValueConverterType<?> getType() {
        return ValueConverters.DIVIDE_CONVERTER;
    }

    @Override
    public Component getDisplayText() {
        return Component.translatable("value_converter.minions.divide.display", divident);
    }

    public static class Type implements ValueConverterType<DivideConverter> {
        @Override
        public MapCodec<DivideConverter> getCodec() {
            return Codec.DOUBLE.xmap(DivideConverter::new, c -> c.divident).fieldOf("divident");
        }

        @Override
        public boolean isSupportedConversion(ValueType<?> from) {
            return (from == ValueTypes.LONG || from == ValueTypes.DOUBLE);
        }

        @Override
        public <F> CompletableFuture<? extends DivideConverter> configure(MinionsGui parent, ValueType<F> from, @Nullable ValueConverter old) {
            Double oldValue = null;
            if(old instanceof DivideConverter ac) {
                oldValue = ac.divident;
            }
            return ValueTypes.DOUBLE.openValueDialog(parent, oldValue)
                    .thenCompose(FutureUtil::failIfNull)
                    .thenApply(DivideConverter::new);
        }
    }
}

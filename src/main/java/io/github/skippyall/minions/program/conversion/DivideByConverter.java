package io.github.skippyall.minions.program.conversion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.gui.input.TextInput;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.ValueConverters;
import io.github.skippyall.minions.registration.ValueTypes;
import io.github.skippyall.minions.util.FutureUtil;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DivideByConverter implements ValueConverter {
    private final double divisor;

    public DivideByConverter(double divisor) {
        this.divisor = divisor;
    }

    @Override
    public Result<? extends TypedValue<?>, Component> convert(TypedValue<?> from) {
        Result<Double, Component> result = Casts.cast(from, ValueTypes.DOUBLE);

        return switch(result) {
            case Result.Success<Double, Component> (Double divident) -> new Result.Success<>(new TypedValue<>(divident / divisor, ValueTypes.DOUBLE));
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
        return Component.translatable("value_converter.minions.divide_by.display", divisor);
    }

    public static class Type implements ValueConverterType<DivideByConverter> {
        @Override
        public MapCodec<DivideByConverter> getCodec() {
            return Codec.DOUBLE.xmap(DivideByConverter::new, c -> c.divisor).fieldOf("divident");
        }

        @Override
        public boolean isSupportedConversion(ValueType<?> from) {
            return (from == ValueTypes.LONG || from == ValueTypes.DOUBLE);
        }

        @Override
        public <F> CompletableFuture<? extends DivideByConverter> configure(MinionsGui parent, ValueType<F> from, @Nullable ValueConverter old) {
            double oldValue = 1.0;
            if(old instanceof DivideByConverter ac) {
                oldValue = ac.divisor;
            }
            return TextInput.input(
                    parent,
                    Component.translatable("value_converter.minions.divide_by.display.input_divisor"),
                    Double.toString(oldValue),
                    string -> {
                        Result<Double, Component> result = Result.wrapCustomError(
                                () -> Double.parseDouble(string),
                                Component.translatable("value_type.minions.double.not_double")
                        );
                        if(result instanceof Result.Success<Double, Component> (Double value) && value == 0) {
                            result = new Result.Error<>(Component.translatable("value_converter.minions.divide.divide_by_zero"));
                        }
                        return CompletableFuture.completedFuture(result);
                    }
            )
                    .thenCompose(FutureUtil::failIfNull)
                    .thenApply(DivideByConverter::new);
        }
    }
}

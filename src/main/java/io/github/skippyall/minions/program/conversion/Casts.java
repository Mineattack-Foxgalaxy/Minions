package io.github.skippyall.minions.program.conversion;

import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.registration.ValueTypes;
import io.github.skippyall.minions.util.TranslationUtil;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class Casts {
    public static <F,T> @Nullable Cast<F,T> getCast(ValueType<F> from, ValueType<T> to) {
        if(from == to) {
            //noinspection unchecked
            return new Cast<>(from, to, v -> (T)v);
        }
        if(from == ValueTypes.LONG && to == ValueTypes.DOUBLE) {
            //noinspection unchecked
            return (Cast<F, T>) new Cast<>(ValueTypes.LONG, ValueTypes.DOUBLE, Long::doubleValue);
        }
        if(from == ValueTypes.DOUBLE && to == ValueTypes.LONG) {
            //noinspection unchecked
            return (Cast<F, T>) new Cast.CastCrafter<>(ValueTypes.DOUBLE, ValueTypes.LONG, Double::longValue).lossy().craftCast();
        }
        if((from == ValueTypes.DOUBLE || from == ValueTypes.LONG) && to == ValueTypes.STRING) {
            //noinspection unchecked
            return (Cast<F, T>) new Cast.CastCrafter<>(from, ValueTypes.STRING, String::valueOf).craftCast();
        }

        return null;
    }

    public static <F,T> @Nullable T cast(TypedValue<F> from, ValueType<T> to) {
        @Nullable Cast<F,T> cast = getCast(from.type(), to);
        if(cast != null) {
            return cast.cast(from.value());
        } else {
            return null;
        }
    }

    public static <F,T> Result<T, Text> castOrError(TypedValue<F> from, ValueType<T> to) {
        return Result.ofNullable(Casts.cast(from, to), () -> Text.translatable(
                "value_converter.minions.cast.cast_failed",
                from.type().getDisplayText(from.value()),
                Text.translatable(TranslationUtil.getTranslationKey(to, MinionRegistries.VALUE_TYPES))
        ));
    }

    public static boolean canCastSafely(ValueType<?> from, ValueType<?> to) {
        if(from == to) {
            return true;
        } else {
            Cast<?,?> cast = getCast(from, to);
            return cast != null && !cast.canFail() && !cast.isLossy();
        }
    }
}

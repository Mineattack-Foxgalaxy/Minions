package io.github.skippyall.minions.program.conversion;

import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.ValueTypes;
import org.jetbrains.annotations.Nullable;

public class Casts {
    public static <F,T> @Nullable Cast<F,T> getCast(ValueType<F> from, ValueType<T> to) {
        if(from == to) {
            //noinspection unchecked
            return new Cast<>(from, to, v -> (T)v);
        }
        if(from == ValueTypes.LONG && to == ValueTypes.DOUBLE) {
            //noinspection unchecked
            return (Cast<F, T>) new Cast<>(ValueTypes.LONG, ValueTypes.DOUBLE, (v) -> (double)v);
        }
        if(from == ValueTypes.DOUBLE && to == ValueTypes.LONG) {
            //noinspection unchecked
            return (Cast<F, T>) new Cast.CastCrafter<>(ValueTypes.DOUBLE, ValueTypes.LONG, (v) -> (long)(double)v).lossy().craftCast();
        }
        if((from == ValueTypes.DOUBLE || from == ValueTypes.LONG) && to == ValueTypes.STRING) {
            //noinspection unchecked
            return (Cast<F, T>) new Cast.CastCrafter<>(from, ValueTypes.STRING, String::valueOf).craftCast();
        }

        return null;
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

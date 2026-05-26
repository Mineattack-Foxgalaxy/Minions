package io.github.skippyall.minions.program.value;

import java.util.Objects;

public record TypedValue<T>(T value, ValueType<T> type) {
    public static <T> TypedValue<T> of(Object o, ValueType<T> type) {
        return new TypedValue<T>(Objects.requireNonNull(type.checkedCast(o)), type);
    }
}

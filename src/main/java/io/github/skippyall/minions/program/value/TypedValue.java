package io.github.skippyall.minions.program.value;

public record TypedValue<T>(T value, ValueType<T> type) {
    public static <T> TypedValue<T> of(Object o, ValueType<T> type) {
        return new TypedValue<>(type.checkedCast(o), type);
    }
}

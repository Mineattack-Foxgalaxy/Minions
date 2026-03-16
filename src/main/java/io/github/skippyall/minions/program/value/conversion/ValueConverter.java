package io.github.skippyall.minions.program.value.conversion;

import io.github.skippyall.minions.program.value.ValueType;

public interface ValueConverter<F,T> {
    T convert(F from);

    ValueType<F> getFrom();

    ValueType<T> getTo();

    ValueConverterType<?> getType();
}

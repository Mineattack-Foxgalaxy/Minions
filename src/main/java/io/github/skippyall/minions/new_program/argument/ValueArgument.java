package io.github.skippyall.minions.new_program.argument;

import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.new_program.value.ValueType;

public class ValueArgument<T> implements Argument<T> {
    private final ValueType<T> valueType;
    private final T value;

    public ValueArgument(ValueType<T> valueType, T value) {
        this.valueType = valueType;
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public T resolve(MinionFakePlayer minion) {
        return value;
    }

    @Override
    public ValueType<T> getType() {
        return valueType;
    }
}

package io.github.skippyall.minions.program.argument;

import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.value.ValueType;

/**
 * An argument that always resolves to a fixed value
 */
public class ValueArgument<T, R extends InstructionRuntime<R>> implements Argument<T, R> {
    private final ValueArgumentType<R> type;
    private final ValueType<T> valueType;
    private final T value;

    public ValueArgument(ValueArgumentType<R> type, ValueType<T> valueType, T value) {
        this.type = type;
        this.valueType = valueType;
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public T resolve(R runtime) {
        return value;
    }

    @Override
    public GuiDisplay getDisplay() {
        return getValueType().display();
    }

    @Override
    public ValueType<T> getValueType() {
        return valueType;
    }

    @Override
    public ValueArgumentType<R> getType() {
        return type;
    }
}

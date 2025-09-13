package io.github.skippyall.minions.program.argument;

import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;

/**
 * An argument that always resolves to a fixed value
 */
public class ValueArgument<T> implements Argument<T, ValueArgumentType<T>> {
    private final ValueArgumentType<T> type;
    private final T value;

    public ValueArgument(ValueArgumentType<T> valueType, T value) {
        this.type = valueType;
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
    public GuiDisplay getDisplay() {
        return getValueType().display();
    }

    @Override
    public ValueArgumentType<T> getType() {
        return type;
    }
}

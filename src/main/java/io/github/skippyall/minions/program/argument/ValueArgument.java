package io.github.skippyall.minions.program.argument;

import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.InstructionRuntime;

/**
 * An argument that always resolves to a fixed value
 */
public class ValueArgument<T, R extends InstructionRuntime<R>> implements Argument<T, ValueArgumentType<T, R>, R> {
    private final ValueArgumentType<T,R> type;
    private final T value;

    public ValueArgument(ValueArgumentType<T,R> valueType, T value) {
        this.type = valueType;
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
    public ValueArgumentType<T,R> getType() {
        return type;
    }
}

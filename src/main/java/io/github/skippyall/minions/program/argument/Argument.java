package io.github.skippyall.minions.program.argument;

import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.value.ValueType;
import org.jetbrains.annotations.Nullable;

/**
 * An <code>Argument</code> can be supplied to an instruction with a matching parameter.
 * Its value is resolved at runtime and can vary between executions.
 * <code>Argument</code>s are created exclusively by <code>SpecificArgumentType</code>s.
 * @param <T> The type of the <code>Argument</code>'s value
 */
public interface Argument<T, R extends InstructionRuntime<R>> {
    T resolve(R minion);

    GuiDisplay getDisplay();

    ValueType<T> getValueType();

    ArgumentType<R> getType();

    default <U,A extends Argument<U,R>> @Nullable A cast(ValueType<U> type) {
        if(getValueType() == type) {
            //noinspection unchecked
            return (A) this;
        } else {
            return null;
        }
    }
}

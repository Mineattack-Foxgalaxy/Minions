package io.github.skippyall.minions.new_program.argument;

import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.new_program.value.ValueType;
import org.jetbrains.annotations.Nullable;

/**
 * An <code>Argument</code> can be supplied to an instruction with a matching parameter.
 * Its value is resolved at runtime and can vary between executions.
 * <code>Argument</code>s are created exclusively by <code>SpecificArgumentType</code>s.
 * @param <T> The type of the <code>Argument</code>'s value
 * @param <S> The <code>SpecificArgumentType</code>
 */
public interface Argument<T, S extends SpecificArgumentType<T, ? extends Argument<T, S>>> {
    T resolve(MinionFakePlayer minion);

    default ValueType<T> getValueType() {
        return getType().getValueType();
    }

    GuiDisplay getDisplay();

    S getType();

    default <U, A extends Argument<U, ? extends SpecificArgumentType<U,A>>> @Nullable A cast(ValueType<U> type) {
        if(getValueType() == type) {
            //noinspection unchecked
            return (A) this;
        } else {
            return null;
        }
    }
}

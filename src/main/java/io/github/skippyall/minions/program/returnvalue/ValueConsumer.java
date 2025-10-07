package io.github.skippyall.minions.program.returnvalue;

import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.argument.SpecificArgumentType;
import io.github.skippyall.minions.program.value.ValueType;
import org.jetbrains.annotations.Nullable;

/**
 * An <code>Argument</code> can be supplied to an instruction with a matching parameter.
 * Its value is resolved at runtime and can vary between executions.
 * <code>Argument</code>s are created exclusively by <code>SpecificArgumentType</code>s.
 * @param <T> The type of the <code>Argument</code>'s value
 * @param <S> The <code>SpecificArgumentType</code>
 */
public interface ValueConsumer<T, S extends SpecificValueConsumerType<T, ? extends ValueConsumer<T, S, R>, R>, R extends InstructionRuntime<R>> {
    void consume(T value, R runtime);

    default ValueType<T> getValueType() {
        return getType().getValueType();
    }

    GuiDisplay getDisplay();

    S getType();

    default <U, A extends io.github.skippyall.minions.program.argument.Argument<U, ? extends SpecificArgumentType<U,A,R>, R>> @Nullable A cast(ValueType<U> type) {
        if(getValueType() == type) {
            //noinspection unchecked
            return (A) this;
        } else {
            return null;
        }
    }
}

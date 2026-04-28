package io.github.skippyall.minions.program.consumer;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;
import org.jetbrains.annotations.Nullable;

/**
 * An <code>ValueSupplier</code> can be supplied to an instruction with a matching parameter.
 * Its value is resolved at runtime and can vary between executions.
 * <code>ValueSupplier</code>s are created exclusively by <code>SpecificArgumentType</code>s.
 * @param <T> The type of the <code>ValueSupplier</code>'s value
 */
public interface ValueConsumer<T,R extends InstructionRuntime<R>> {
    void consume(T value, R runtime);

    ValueType<T> getValueType();

    ValueConsumerType<R> getType();

    default <U,A extends ValueConsumer<U,R>> @Nullable A cast(ValueType<U> type) {
        if(getValueType() == type) {
            //noinspection unchecked
            return (A) this;
        } else {
            return null;
        }
    }

    static <R extends InstructionRuntime<R>> Codec<ValueConsumer<?,R>> createValueConsumerCodec(Codec<ValueConsumerType<R>> codec) {
        return codec.dispatch(
                "type",
                ValueConsumer::getType,
                type ->
                        MinionRegistries.VALUE_TYPES.getCodec().<ValueConsumer<?,R>>dispatch(
                                ValueConsumer::getValueType,
                                valueType -> type.getCodec(valueType).fieldOf("valueType")
                        ).fieldOf("valueType")
        );
    }
}

package io.github.skippyall.minions.program.consumer;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;
import org.jspecify.annotations.Nullable;

/**
 * An <code>ValueSupplier</code> can be supplied to an instruction with a matching parameter.
 * Its value is resolved at runtime and can vary between executions.
 * <code>ValueSupplier</code>s are created exclusively by <code>SpecificArgumentType</code>s.
 * @param <T> The type of the <code>ValueSupplier</code>'s value
 */
public interface ValueConsumer<T> {
    void consume(T value);

    ValueType<T> getValueType();

    ValueConsumerType getType();

    default <U,A extends ValueConsumer<U>> @Nullable A cast(ValueType<U> type) {
        if(getValueType() == type) {
            //noinspection unchecked
            return (A) this;
        } else {
            return null;
        }
    }

    static Codec<ValueConsumer<?>> createValueConsumerCodec(Codec<ValueConsumerType> codec) {
        return codec.dispatch(
                "type",
                ValueConsumer::getType,
                type ->
                        MinionRegistries.VALUE_TYPES.byNameCodec().<ValueConsumer<?>>dispatch(
                                ValueConsumer::getValueType,
                                valueType -> type.getCodec(valueType).fieldOf("valueType")
                        ).fieldOf("valueType")
        );
    }
}

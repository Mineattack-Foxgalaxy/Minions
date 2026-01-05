package io.github.skippyall.minions.program.supplier;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.MinionRegistries;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.value.ValueType;
import org.jetbrains.annotations.Nullable;

/**
 * An <code>ValueSupplier</code> can be supplied to an instruction with a matching parameter.
 * Its value is resolved at runtime and can vary between executions.
 * <code>ValueSupplier</code>s are created exclusively by <code>SpecificArgumentType</code>s.
 * @param <T> The type of the <code>ValueSupplier</code>'s value
 */
public interface ValueSupplier<T, R extends InstructionRuntime<R>> {
    T resolve(R minion);

    ValueType<T> getValueType();

    ValueSupplierType<R> getType();

    default <U,A extends ValueSupplier<U,R>> @Nullable A cast(ValueType<U> type) {
        if(getValueType() == type) {
            //noinspection unchecked
            return (A) this;
        } else {
            return null;
        }
    }

    static <R extends InstructionRuntime<R>> Codec<ValueSupplier<?,R>> createArgumentCodec(Codec<ValueSupplierType<R>> codec) {
        return codec.dispatch(
                "type",
                ValueSupplier::getType,
                type ->
                        MinionRegistries.VALUE_TYPES.getCodec().<ValueSupplier<?,R>>dispatch(
                                ValueSupplier::getValueType,
                                valueType -> type.getCodec(valueType).fieldOf("valueType")
                        ).fieldOf("valueType")
        );
    }
}

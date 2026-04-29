package io.github.skippyall.minions.program.supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;
import net.minecraft.network.chat.Component;
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

    Component getDisplayText();

    default <U,A extends ValueSupplier<U,R>> @Nullable A cast(ValueType<U> type) {
        if(getValueType() == type) {
            //noinspection unchecked
            return (A) this;
        } else {
            return null;
        }
    }

    /**
     * WARNING: If originalType is not the type of the value suppliers from the codec, this will leak wrong generics!
     */
    static <T, U, R extends InstructionRuntime<R>, A extends ValueSupplier<U,R>, C extends ValueSupplier<T, R>> @Nullable Codec<A> castCodec(Codec<C> codec, ValueType<T> originalType, ValueType<U> newType) {
        if(originalType == newType) {
            //noinspection unchecked
            return (Codec<A>) codec;
        } else {
            return null;
        }
    }

    static <R extends InstructionRuntime<R>> Codec<ValueSupplier<?,R>> createArgumentCodec(Codec<ValueSupplierType<R>> codec) {
        return codec.dispatch(
                "type",
                ValueSupplier::getType,
                type ->
                        MinionRegistries.VALUE_TYPES.byNameCodec().<ValueSupplier<?,R>>partialDispatch(
                                "type",
                                s -> DataResult.success(s.getValueType()),
                                valueType -> {
                                    if(type.getCodec(valueType) != null) {
                                        return DataResult.success(type.getCodec(valueType).fieldOf("valueType"));
                                    } else {
                                        return DataResult.error(() -> "Supplier type " + type + "not available for value type " + valueType);
                                    }
                                }
                        ).fieldOf("valueType")
        );
    }
}

package io.github.skippyall.minions.program.supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

/**
 * An <code>ValueSupplier</code> can be supplied to an instruction with a matching parameter.
 * Its value is resolved at runtime and can vary between executions.
 * <code>ValueSupplier</code>s are created exclusively by <code>SpecificArgumentType</code>s.
 * @param <T> The type of the <code>ValueSupplier</code>'s value
 */
public interface ValueSupplier<T> {
    Codec<ValueSupplier<?>> CODEC = MinionRegistries.VALUE_SUPPLIER_TYPES.byNameCodec().dispatch(
            "type",
            ValueSupplier::getType,
            type -> type.getCodec().fieldOf("data")
    );

    T resolve(MinecraftServer server);

    ValueType<T> getValueType();

    ValueSupplierType getType();

    Component getDisplayText();

    default <U,A extends ValueSupplier<U>> @Nullable A cast(ValueType<U> type) {
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
    static <T, U, R extends InstructionRuntime, A extends ValueSupplier<U>, C extends ValueSupplier<T>> @Nullable Codec<A> castCodec(Codec<C> codec, ValueType<T> originalType, ValueType<U> newType) {
        if(originalType == newType) {
            //noinspection unchecked
            return (Codec<A>) codec;
        } else {
            return null;
        }
    }
}

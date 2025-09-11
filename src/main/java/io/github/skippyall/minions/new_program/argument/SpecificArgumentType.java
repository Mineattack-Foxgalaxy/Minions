package io.github.skippyall.minions.new_program.argument;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.new_program.value.ValueType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * <code>SpecificArgumentType</code>s create <code>Argument</code>s of the specified <code>ValueType</code>.
 * They are also responsible for the serialization and user input of arguments.
 * @param <V> The value type of the <code>Argument</code>s to create
 * @param <A> The type of the <code>Argument</code>s themselves
 */
public abstract class SpecificArgumentType<V, A extends Argument<V, ? extends SpecificArgumentType<V, A>>> {
    protected final ValueType<V> valueType;

    public SpecificArgumentType(ValueType<V> valueType) {
        this.valueType = valueType;
    }

    public abstract GenericArgumentType getGenericArgumentType();

    public abstract Codec<A> getArgumentCodec();

    public abstract CompletableFuture<A> openArgumentDialog(ServerPlayerEntity player, @Nullable A previousArgument);

    public ValueType<V> getValueType() {
        return valueType;
    }
}

package io.github.skippyall.minions.program.returnvalue;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.argument.Argument;
import io.github.skippyall.minions.program.argument.GenericArgumentType;
import io.github.skippyall.minions.program.value.ValueType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * <code>SpecificArgumentType</code>s create <code>Argument</code>s of the specified <code>ValueType</code>.
 * They are also responsible for the serialization and user input of arguments.
 * @param <V> The value type of the <code>Argument</code>s to create
 * @param <A> The type of the <code>Argument</code>s themselves
 */
public abstract class SpecificValueConsumerType<V, A extends ValueConsumer<V, ? extends SpecificValueConsumerType<V, A, R>, R>, R extends InstructionRuntime<R>> {
    public final ValueType<V> valueType;
    public final GenericArgumentType<R> genericType;

    public SpecificValueConsumerType(ValueType<V> valueType, GenericArgumentType<R> genericType) {
        this.valueType = valueType;
        this.genericType = genericType;
    }

    public abstract Codec<A> getValueConsumerCodec();

    public abstract CompletableFuture<A> openValueConsumerDialog(ServerPlayerEntity player, @Nullable A previousArgument);

    public ValueType<V> getValueType() {
        return valueType;
    }

    public GenericArgumentType<R> getGenericArgumentType() {
        return genericType;
    }
}

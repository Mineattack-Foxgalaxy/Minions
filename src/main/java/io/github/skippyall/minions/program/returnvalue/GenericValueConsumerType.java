package io.github.skippyall.minions.program.returnvalue;

import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.argument.Argument;
import io.github.skippyall.minions.program.argument.SpecificArgumentType;
import io.github.skippyall.minions.program.value.ValueType;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

public interface GenericValueConsumerType<R extends InstructionRuntime<R>> {
    default <V> SpecificValueConsumerType<V, ? extends ValueConsumer<V, ?, R>, R> createTypeSpecific(ValueType<V> valueType) {
        return createTypeSpecific(valueType, this);
    }

    <V> SpecificValueConsumerType<V, ? extends Argument<V, ?, R>, R> createTypeSpecific(ValueType<V> valueType, GenericValueConsumerType<R> me);

    default boolean canCreate(ValueType<?> valueType) {
        return true;
    }
}

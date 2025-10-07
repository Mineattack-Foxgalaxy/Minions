package io.github.skippyall.minions.program.argument;

import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.value.ValueType;

public interface GenericArgumentType<R extends InstructionRuntime<R>> {
    default <V> SpecificArgumentType<V, ? extends Argument<V, ?, R>, R> createTypeSpecific(ValueType<V> valueType) {
        return createTypeSpecific(valueType, this);
    }

    <V> SpecificArgumentType<V, ? extends Argument<V, ?, R>, R> createTypeSpecific(ValueType<V> valueType, GenericArgumentType<R> me);

    default boolean canCreate(ValueType<?> valueType) {
        return true;
    }
}

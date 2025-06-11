package io.github.skippyall.minions.new_program.argument;

import io.github.skippyall.minions.new_program.value.ValueType;

public interface GenericArgumentType {
    <V> SpecificArgumentType<V, ? extends Argument<V>> createTypeSpecific(ValueType<V> valueType);
}

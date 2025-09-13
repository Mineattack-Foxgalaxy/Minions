package io.github.skippyall.minions.program.argument;

import io.github.skippyall.minions.program.value.ValueType;

public interface GenericArgumentType {
    <V> SpecificArgumentType<V, ? extends Argument<V,?>> createTypeSpecific(ValueType<V> valueType);
}

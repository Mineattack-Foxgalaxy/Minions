package io.github.skippyall.minions.program.handler;

import io.github.skippyall.minions.program.value.TypedValue;

import java.util.HashMap;
import java.util.Map;

public class ParameterValueList {
    private final Map<Parameter<?>, Object> values;

    public ParameterValueList() {
        values = new HashMap<>();
    }

    public <T> T getValue(Parameter<T> parameter) {
        //noinspection unchecked
        return (T) values.get(parameter);
    }

    public <T> TypedValue<T> getTypedValue(Parameter<T> parameter) {
        return TypedValue.of(getValue(parameter), parameter.type());
    }

    public <T> void setValue(Parameter<T> parameter, T value) {
        if(!parameter.type().isOf(value)) {
            throw new IllegalArgumentException("Tried to set value of type " + value.getClass().getName() + "for parameter " + parameter.name() + "of type " + parameter.type());
        }
        values.put(parameter, value);
    }
}

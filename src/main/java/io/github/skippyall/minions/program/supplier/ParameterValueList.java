package io.github.skippyall.minions.program.supplier;

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

    public <T> void setValue(Parameter<T> parameter, T value) {
        values.put(parameter, value);
    }
}

package io.github.skippyall.minions.new_program;

import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.new_program.argument.Argument;
import io.github.skippyall.minions.new_program.value.ValueType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ParameterList {
    Map<String, Argument<?>> parameters = new HashMap<>();

    public <T> @Nullable T getValue(String name, ValueType<T> valueType, MinionFakePlayer minion) {
        Argument<T> argument = getArgument(name, valueType);
        return argument != null ? argument.resolve(minion) : null;
    }

    public <T> @Nullable Argument<T> getArgument(String name, ValueType<T> valueType) {
        Argument<?> argument = parameters.get(name);
        if(argument != null && argument.getType() == valueType) {
            //noinspection unchecked
            return (Argument<T>) argument;
        } else {
            return null;
        }
    }

    public void setArgument(String name, Argument<?> argument) {
        parameters.put(name, argument);
    }
}

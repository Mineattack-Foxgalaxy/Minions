package io.github.skippyall.minions.program.variables;

import io.github.skippyall.minions.program.argument.Arg;
import io.github.skippyall.minions.program.runtime.ProgramRuntime;

public class Variable<T> implements Arg<T>, ValueStorage<T> {
    private final Type<T> type;
    private final String name;

    public Variable(Type<T> type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public T resolve(ProgramRuntime runtime) {
        return type.cast(runtime.getVariable(name));
    }

    @Override
    public Type<T> getType() {
        return type;
    }

    @Override
    public void storeValue(T value, ProgramRuntime runtime) {
        runtime.setVariable(name, value);
    }
}

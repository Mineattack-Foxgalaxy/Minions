package io.github.skippyall.minions.program.variables;

import io.github.skippyall.minions.program.statement.Arg;

public class Variable implements Arg {
    private final Type type;
    private final String name;
    private Object value;

    public Variable(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public Type getType() {
        return type;
    }
}

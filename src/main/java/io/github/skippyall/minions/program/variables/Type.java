package io.github.skippyall.minions.program.variables;

public abstract class Type<T> {
    @SuppressWarnings("unchecked")
    public T cast(Object object) throws ClassCastException {
        return (T) object;
    }
}

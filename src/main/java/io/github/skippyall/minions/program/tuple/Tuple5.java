package io.github.skippyall.minions.program.tuple;

import java.util.List;

public record Tuple5<T0, T1, T2, T3, T4>(T0 v0, T1 v1, T2 v2, T3 v3, T4 v4) implements Tuple {
    @Override
    public <T> Tuple add(T value) {
        throw new UnsupportedOperationException("Cannot add element to length 5 tuple.");
    }

    @Override
    public Tuple removeLast() {
        return new Tuple4<>(v0, v1, v2, v3);
    }

    @Override
    public List<Object> getValueList() {
        return List.of(v0, v1, v2, v3, v4);
    }

    @Override
    public int size() {
        return 5;
    }
}

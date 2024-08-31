package io.github.skippyall.minions.program.tuple;

import java.util.List;

public record Tuple4<T0, T1, T2, T3, T4>(T0 v0, T1 v1, T2 v2, T3 v3) implements Tuple {
    @Override
    public <T> Tuple add(T value) {
        return new Tuple5<>(v0, v1, v2, v3, value);
    }

    @Override
    public Tuple removeLast() {
        return new Tuple3<>(v0, v1, v2);
    }

    @Override
    public List<Object> getValueList() {
        return List.of(v0, v1, v2, v3);
    }

    @Override
    public int size() {
        return 4;
    }
}

package io.github.skippyall.minions.program.tuple;

import java.util.List;

public record Tuple3<T0, T1, T2>(T0 v0, T1 v1, T2 v2) implements Tuple {
    @Override
    public <T> Tuple add(T value) {
        return new Tuple4<>(v0, v1, v2, value);
    }

    @Override
    public Tuple removeLast() {
        return new Tuple2<>(v0, v1);
    }

    @Override
    public List<Object> getValueList() {
        return List.of(v0, v1, v2);
    }

    @Override
    public int size() {
        return 3;
    }
}

package io.github.skippyall.minions.program.tuple;

import java.util.List;

public record Tuple2<T0, T1>(T0 v0, T1 v1) implements Tuple {
    @Override
    public <T> Tuple add(T value) {
        return new Tuple3<>(v0, v1, value);
    }

    @Override
    public Tuple removeLast() {
        return new Tuple1<>(v0);
    }

    @Override
    public List<Object> getValueList() {
        return List.of(v0, v1);
    }

    @Override
    public int size() {
        return 2;
    }
}

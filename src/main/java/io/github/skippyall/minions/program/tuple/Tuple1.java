package io.github.skippyall.minions.program.tuple;

import java.util.List;

public record Tuple1<T0>(T0 v0) implements Tuple{
    @Override
    public <T> Tuple add(T value) {
        return new Tuple2<>(v0, value);
    }

    @Override
    public Tuple removeLast() {
        return new Tuple0();
    }

    @Override
    public List<Object> getValueList() {
        return List.of(v0);
    }

    @Override
    public int size() {
        return 1;
    }
}

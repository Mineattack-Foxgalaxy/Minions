package io.github.skippyall.minions.program.tuple;

import java.util.List;

public record Tuple0() implements Tuple{
    @Override
    public <T> Tuple add(T value) {
        return new Tuple1<>(value);
    }

    @Override
    public Tuple removeLast() {
        throw new UnsupportedOperationException("Cannot remove element from length 0 tuple.");
    }

    @Override
    public List<Object> getValueList() {
        return List.of();
    }

    @Override
    public int size() {
        return 0;
    }
}

package io.github.skippyall.minions.program.tuple;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public interface Tuple extends Iterable<Object>{
    <T> Tuple add(T value);
    Tuple removeLast();
    List<Object> getValueList();
    int size();

    @NotNull
    @Override
    default Iterator<Object> iterator() {
        return getValueList().iterator();
    }

    static Tuple ofList(List<?> list) {
        return switch (list.size()) {
            case 0 -> new Tuple0();
            case 1 -> new Tuple1<Object>(list.get(0));
            case 2 -> new Tuple2<Object, Object>(list.get(0), list.get(1));
            case 3 -> new Tuple3<Object, Object, Object>(list.get(0), list.get(1), list.get(2));
            default -> throw new UnsupportedOperationException();
        };
    }
}

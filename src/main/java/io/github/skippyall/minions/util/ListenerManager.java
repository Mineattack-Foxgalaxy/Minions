package io.github.skippyall.minions.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class ListenerManager<T> {
    protected final List<T> listeners = new CopyOnWriteArrayList<>();

    public void forEachListener(Consumer<T> listenerConsumer) {
        for(T listener : listeners) {
            listenerConsumer.accept(listener);
        }
    }

    public void addListener(T listener) {
        listeners.add(listener);
    }

    public void removeListener(T listener) {
        listeners.remove(listener);
    }
}

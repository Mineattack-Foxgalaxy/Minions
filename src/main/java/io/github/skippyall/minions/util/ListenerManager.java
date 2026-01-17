package io.github.skippyall.minions.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class ListenerManager<T> {
    protected final List<T> listeners;

    public ListenerManager() {
        this(new CopyOnWriteArrayList<>());
    }

    protected ListenerManager(List<T> listeners) {
        this.listeners = listeners;
    }

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

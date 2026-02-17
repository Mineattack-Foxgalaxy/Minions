package io.github.skippyall.minions.listener;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class ListenerManager<T> implements Iterable<T> {
    protected final Set<T> listeners;

    public ListenerManager() {
        this(new CopyOnWriteArraySet<>());
    }

    protected ListenerManager(Set<T> listeners) {
        this.listeners = listeners;
    }

    public void addListener(T listener) {
        listeners.add(listener);
    }

    public void removeListener(T listener) {
        listeners.remove(listener);
    }

    public boolean hasListener(T listener) {
        return listeners.contains(listener);
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return listeners.iterator();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}

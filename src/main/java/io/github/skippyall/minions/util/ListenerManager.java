package io.github.skippyall.minions.util;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ListenerManager<T> implements Iterable<T> {
    protected final List<T> listeners;

    public ListenerManager() {
        this(new CopyOnWriteArrayList<>());
    }

    protected ListenerManager(List<T> listeners) {
        this.listeners = listeners;
    }

    public void addListener(T listener) {
        listeners.add(listener);
    }

    public void removeListener(T listener) {
        listeners.remove(listener);
        onRemove(listener);
    }

    protected void onRemove(T listener) {}

    @Override
    public @NotNull Iterator<T> iterator() {
        return new Iterator<>() {
            final Iterator<T> backing = listeners.iterator();
            T last;

            @Override
            public boolean hasNext() {
                return backing.hasNext();
            }

            @Override
            public T next() {
                last = backing.next();
                return last;
            }

            @Override
            public void remove() {
                backing.remove();
                onRemove(last);
            }
        };
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}

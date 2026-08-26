package io.github.skippyall.minions.listener;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class ListenerManager<T> implements Iterable<T> {
    protected final Set<T> listeners;
    private final Runnable onChange;

    protected ListenerManager(CopyOnWriteArraySet<T> listeners, Runnable onChange) {
        this.listeners = listeners;
        this.onChange = onChange;
    }

    public ListenerManager(Runnable onChange) {
        this(new CopyOnWriteArraySet<>(), onChange);
    }

    public ListenerManager() {
        this(new CopyOnWriteArraySet<>(), () -> {});
    }

    public void addListener(T listener) {
        listeners.add(listener);
        onChange.run();
    }

    public void removeListener(T listener) {
        listeners.remove(listener);
        onChange.run();
    }

    public Iterator<T> iterator() {
        Iterator<T> iterator = listeners.iterator();
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return iterator.next();
            }

            @Override
            public void remove() {
                iterator.remove();
                onChange.run();
            }
        };
    }
}

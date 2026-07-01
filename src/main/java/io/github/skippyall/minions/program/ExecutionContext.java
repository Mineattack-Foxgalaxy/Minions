package io.github.skippyall.minions.program;

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

public class ExecutionContext {
    private final Map<Key<?>, Object> map;

    public ExecutionContext() {
        map = new IdentityHashMap<>();
    }

    protected ExecutionContext(Map<Key<?>, Object> map) {
        this.map = map;
    }

    @Nullable
    public <T> T get(Key<T> key) {
        //noinspection unchecked
        return (T) map.get(key);
    }

    public <T> T getOrThrow(Key<T> key) {
        T value = get(key);
        if(value != null) {
            return value;
        } else {
            throw new NoSuchElementException("Key " + key + " is not in map");
        }
    }

    public <T> void put(Key<T> key, T value) {
        map.put(key, value);
    }

    public static class Key<T> {
        private final Identifier id;

        public Key(Identifier id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Key<?> key)) return false;
            return Objects.equals(id, key.id);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }

        @Override
        public String toString() {
            return "ExecutionContext.Key[" + id + "]";
        }
    }
}

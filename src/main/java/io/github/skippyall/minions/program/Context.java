package io.github.skippyall.minions.program;

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

public class Context {
    public static final Context EMPTY = new Context();

    private final Map<Key<?>, Object> map;

    private Context() {
        this.map = Map.of();
    }

    private Context(Map<Key<?>, Object> map) {
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
            throw new NoSuchElementException("Key " + key + " is not in the context");
        }
    }

    public Builder toBuilder() {
        return new Builder(new HashMap<>(map));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(Predicate<Key<?>> keyPredicate) {
        return new Builder();
    }

    public static class Builder {
        private final Map<Key<?>, Object> map;
        private final Predicate<Key<?>> keyPredicate;

        private boolean built;

        private Builder() {
            this(new HashMap<>());
        }

        private Builder(Map<Key<?>, Object> map) {
            this(map, k -> true);
        }

        private Builder(Map<Key<?>, Object> map, Predicate<Key<?>> keyPredicate) {
            this.map = map;
            this.keyPredicate = keyPredicate;
        }

        public <T> Builder put(Key<T> key, T value) {
            if(built) {
                throw new IllegalStateException("Builder is already built");
            }
            if(!keyPredicate.test(key)) {
                throw new IllegalArgumentException("Key " + key + " is not allowed for this context");
            }
            map.put(key, value);
            return this;
        }

        public Builder remove(Key<?> key) {
            if(built) {
                throw new IllegalStateException("Builder is already built");
            }
            map.remove(key);
            return this;
        }

        public Context build() {
            built = true;
            return new Context(map);
        }
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
            return "Context.Key[" + id + "]";
        }
    }
}

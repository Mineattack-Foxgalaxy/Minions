package io.github.skippyall.minions.listener;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArraySet;

public class SerializableListenerManager<T> extends ListenerManager<T> {
    public SerializableListenerManager(CopyOnWriteArraySet<T> listeners, Runnable onChange) {
        super(listeners, onChange);
    }

    public SerializableListenerManager(Runnable onChange) {
        super(onChange);
    }

    public SerializableListenerManager() {
        super();
    }

    public interface SerializableListener {
        default Optional<Identifier> getCodecId() {
            return Optional.empty();
        }
    }

    public static <T extends SerializableListener> Codec<SerializableListenerManager<T>> getCodec(Registry<Codec<? extends T>> registry) {
        return getCodec(registry, () -> {});
    }

    public static <T extends SerializableListener> Codec<SerializableListenerManager<T>> getCodec(Registry<Codec<? extends T>> registry, Runnable onChange) {
        return registry.byNameCodec().<T>dispatch(
                listener -> listener.getCodecId().map(registry::getValue).orElseThrow(),
                codec -> codec.fieldOf("data")
        ).listOf().xmap(
                list -> new SerializableListenerManager<>(new CopyOnWriteArraySet<>(list), onChange),
                manager -> {
                    List<T> serializableListeners = new ArrayList<>();
                    for (T listener : manager) {
                        if (listener.getCodecId().isPresent()) {
                            serializableListeners.add(listener);
                        }
                    }
                    return serializableListeners;
                }
        );
    }
}

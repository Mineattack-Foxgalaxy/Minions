package io.github.skippyall.minions.listener;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class SerializableListenerManager<T extends SerializableListenerManager.SerializableListener> extends ListenerManager<T> {
    public SerializableListenerManager() {
        super();
    }

    protected SerializableListenerManager(Set<T> listeners) {
        super(listeners);
    }

    public static <T extends SerializableListener> Codec<SerializableListenerManager<T>> getCodec(Registry<Codec<? extends T>> registry) {
        return registry.byNameCodec().<T>dispatch(
                    listener -> listener.getCodecId().map(registry::getValue).orElse(MapCodec.unitCodec(null)),
                    codec -> codec.fieldOf("data")
            ).listOf().xmap(
                    list -> new SerializableListenerManager<>(new CopyOnWriteArraySet<>(list)),
                    manager -> {
                        List<T> serializableListeners = new ArrayList<>();
                        for(T listener : manager.listeners) {
                            if(listener.getCodecId().isPresent()) {
                                serializableListeners.add(listener);
                            }
                        }
                        return serializableListeners;
                    }
            );
    }

    public interface SerializableListener {
        default Optional<Identifier> getCodecId() {
            return Optional.empty();
        }
    }
}

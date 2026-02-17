package io.github.skippyall.minions.listener;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registry;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class SerializableListenerManager<T extends SerializableListenerManager.SerializableListener> extends ListenerManager<T> {
    private final Registry<Codec<? extends T>> registry;

    public SerializableListenerManager(Registry<Codec<? extends T>> registry) {
        this.registry = registry;
    }

    private SerializableListenerManager(Registry<Codec<? extends T>> registry, Set<T> listeners) {
        super(listeners);
        this.registry = registry;
    }

    public static <T extends SerializableListener> Codec<SerializableListenerManager<T>> getCodec(Registry<Codec<? extends T>> registry) {
        return registry.getCodec().<T>dispatch(
                    listener -> listener.getCodecId().map(registry::get).orElse(Codec.unit(null)),
                    codec -> codec.fieldOf("data")
            ).listOf().xmap(
                    list -> new SerializableListenerManager<>(registry, new CopyOnWriteArraySet<>(list)),
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

    public void save(WriteView view) {
        WriteView.ListView listView = view.getList("listeners");
        for (T listener : listeners) {
            if(listener.getCodecId().isPresent()) {
                WriteView listenerView = listView.add();
                Codec<? extends T> codec = registry.get(listener.getCodecId().get());
                listenerView.put("id", Identifier.CODEC, listener.getCodecId().get());
                //noinspection unchecked
                listenerView.put("data", (Codec<T>) codec, listener);
            }
        }
    }

    public void load(ReadView view) {
        ReadView.ListReadView listView = view.getListReadView("listeners");
        for (ReadView listenerView : listView) {
            Optional<Identifier> id = listenerView.read("id", Identifier.CODEC);
            if(id.isEmpty()) {
                continue;
            }

            Codec<? extends T> codec = registry.get(id.get());

            Optional<? extends T> listener = listenerView.read("data", codec);

            if(listener.isPresent()) {
                listeners.add(listener.get());
            }
        }
    }

    public interface SerializableListener {
        default Optional<Identifier> getCodecId() {
            return Optional.empty();
        }
    }
}

package io.github.skippyall.minions.util;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registry;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class SerializableListenerManager<T extends SerializableListenerManager.SerializableListener> extends ListenerManager<T> {
    private final Registry<Codec<? extends T>> registry;

    public SerializableListenerManager(Registry<Codec<? extends T>> registry) {
        this.registry = registry;
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

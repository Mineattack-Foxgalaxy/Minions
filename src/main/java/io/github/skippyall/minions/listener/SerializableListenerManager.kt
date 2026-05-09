package io.github.skippyall.minions.listener

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import io.github.skippyall.minions.listener.SerializableListenerManager.SerializableListener
import net.minecraft.core.Registry
import net.minecraft.resources.Identifier
import java.util.Optional
import java.util.concurrent.CopyOnWriteArraySet

class SerializableListenerManager<T : SerializableListener>(
    listeners: MutableSet<T> = CopyOnWriteArraySet(),
    onChange: () -> Unit = {},
) : ListenerManager<T>(listeners, onChange) {

    interface SerializableListener {
        val codecId: Optional<Identifier>
            get() = Optional.empty<Identifier>()
    }

    companion object {
        @JvmStatic
        @JvmOverloads
        fun <T : SerializableListener> getCodec(
            registry: Registry<Codec<out T>>,
            onChange: () -> Unit = {},
        ): Codec<SerializableListenerManager<T>> {
            return registry.byNameCodec().dispatch(
                { listener -> listener.codecId.map(registry::getValue)
                        .orElseGet { MapCodec.unitCodec(null) }
                },
                { codec -> codec.fieldOf("data") }
            ).listOf().xmap(
                { list -> SerializableListenerManager<T>(CopyOnWriteArraySet<T>(list), onChange) },
                { manager ->
                    val serializableListeners: MutableList<T> = mutableListOf()
                    for (listener in manager.listeners) {
                        if (listener.codecId.isPresent) {
                            serializableListeners.add(listener)
                        }
                    }
                    return@xmap serializableListeners
                }
            )
        }
    }
}

package io.github.skippyall.minions.minion

import com.mojang.authlib.properties.PropertyMap
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.github.skippyall.minions.listener.SerializableListenerManager
import io.github.skippyall.minions.registration.MinionRegistries
import net.minecraft.core.UUIDUtil
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ExtraCodecs
import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class MinionData(
    initialUuid: UUID,
    initialName: String,
    initialSkin: Optional<PropertyMap>,
    initialIsSpawned: Boolean,
    val listeners: SerializableListenerManager<MinionListener>,
    initialConfig: MinionConfig,
    var onDirty: Runnable = {},
) {
    var uuid by DirtyProperty(initialUuid, this::setDirty)
    var name by DirtyProperty(initialName, this::setDirty)
    var skin by DirtyProperty(initialSkin, this::setDirty)
    var isSpawned by DirtyProperty(initialIsSpawned, this::setDirty)
    var config by DirtyProperty(initialConfig, this::setDirty)

    fun setDirty() {
        this@MinionData.onDirty.run()
    }

    companion object {
        @JvmField
        val CODEC: Codec<MinionData> =
            RecordCodecBuilder.create { instance ->
                instance.group(
                    UUIDUtil.AUTHLIB_CODEC.fieldOf("uuid").forGetter(MinionData::uuid),
                    Codec.STRING.fieldOf("name").forGetter(MinionData::name),
                    ExtraCodecs.PROPERTY_MAP.optionalFieldOf("skin").forGetter(MinionData::skin),
                    Codec.BOOL.optionalFieldOf("isSpawned", false).forGetter(MinionData::isSpawned),
                    SerializableListenerManager.getCodec<MinionListener>(MinionRegistries.MINION_LISTENER_CODECS)
                            .optionalFieldOf("listeners").xmap(
                                { optional -> optional.orElseGet { SerializableListenerManager() } },
                                Optional<SerializableListenerManager<MinionListener>>::of
                            ).forGetter(MinionData::listeners),
                    MinionConfig.CODEC.optionalFieldOf("config", MinionConfig())
                            .forGetter(MinionData::config)
                ).apply(
                    instance,
                    ::MinionData
                )
            }

        @JvmStatic
        fun createDefault(server: MinecraftServer): MinionData {
            return MinionData(
                UUID.randomUUID(),
                MinionProfileUtils.newDefaultMinionName(server),
                Optional.empty<PropertyMap>(),
                false,
                SerializableListenerManager(),
                MinionConfig()
            ) {
                MinionPersistentState.get(server).isDirty = true
            }
        }
    }

    class DirtyProperty<V>(var value: V, val onDirty: () -> Unit) : ReadWriteProperty<Any?, V> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): V {
            return value
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
            this.value = value
            onDirty()
        }
    }
}

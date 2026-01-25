package io.github.skippyall.minions.minion;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.util.SerializableListenerManager;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.dynamic.Codecs;

import java.util.Optional;
import java.util.UUID;

public record MinionData(UUID uuid, String name, Optional<PropertyMap> skin, boolean isSpawned, SerializableListenerManager<MinionListener> listeners) {
    public static final Codec<MinionData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Uuids.CODEC.fieldOf("uuid").forGetter(MinionData::uuid),
                    Codec.STRING.fieldOf("name").forGetter(MinionData::name),
                    Codecs.GAME_PROFILE_PROPERTY_MAP.optionalFieldOf("skin").forGetter(MinionData::skin),
                    Codec.BOOL.optionalFieldOf("isSpawned", false).forGetter(MinionData::isSpawned),
                    SerializableListenerManager.getCodec(MinionRegistries.MINION_LISTENER_CODECS).optionalFieldOf("listeners").xmap(
                            optional -> optional.orElseGet(() -> new SerializableListenerManager<>(MinionRegistries.MINION_LISTENER_CODECS)),
                            Optional::of
                    ).forGetter(MinionData::listeners)
            ).apply(instance, MinionData::new)
    );

    public static final ComponentType<UUID> COMPONENT = Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(Minions.MOD_ID, "minion_data"), ComponentType.<UUID>builder().codec(Uuids.CODEC).build());

    public static MinionData createDefault(MinecraftServer server) {
        return new MinionData(UUID.randomUUID(), MinionProfileUtils.newDefaultMinionName(server), Optional.empty(), false, new SerializableListenerManager<>(MinionRegistries.MINION_LISTENER_CODECS));
    }

    public MinionData withName(String name) {
        return new MinionData(uuid, name, skin, isSpawned, listeners);
    }

    public MinionData withSkin(Optional<PropertyMap> skin) {
        return new MinionData(uuid, name, skin, isSpawned, listeners);
    }

    public MinionData withSpawned(boolean isSpawned) {
        return new MinionData(uuid, name, skin, isSpawned, listeners);
    }

    public static void register() {
        PolymerComponent.registerDataComponent(COMPONENT);
    }
}

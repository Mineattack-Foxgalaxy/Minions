package io.github.skippyall.minions.minion;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.listener.SerializableListenerManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Uuids;
import net.minecraft.util.dynamic.Codecs;

import java.util.Optional;
import java.util.UUID;

public record MinionData(
        UUID uuid,
        String name,
        Optional<PropertyMap> skin,
        boolean isSpawned,
        SerializableListenerManager<MinionListener> listeners,
        MinionConfig config
) {
    public static final Codec<MinionData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Uuids.CODEC.fieldOf("uuid").forGetter(MinionData::uuid),
                    Codec.STRING.fieldOf("name").forGetter(MinionData::name),
                    Codecs.GAME_PROFILE_PROPERTY_MAP.optionalFieldOf("skin").forGetter(MinionData::skin),
                    Codec.BOOL.optionalFieldOf("isSpawned", false).forGetter(MinionData::isSpawned),
                    SerializableListenerManager.getCodec(MinionRegistries.MINION_LISTENER_CODECS).optionalFieldOf("listeners").xmap(
                            optional -> optional.orElseGet(() -> new SerializableListenerManager<>(MinionRegistries.MINION_LISTENER_CODECS)),
                            Optional::of
                    ).forGetter(MinionData::listeners),
                    MinionConfig.CODEC.optionalFieldOf("config", new MinionConfig()).forGetter(MinionData::config)
            ).apply(instance, MinionData::new)
    );

    public static MinionData createDefault(MinecraftServer server) {
        return new MinionData(
                UUID.randomUUID(),
                MinionProfileUtils.newDefaultMinionName(server),
                Optional.empty(),
                false,
                new SerializableListenerManager<>(MinionRegistries.MINION_LISTENER_CODECS),
                new MinionConfig()
        );
    }

    public MinionData withName(String name) {
        return new MinionData(uuid, name, skin, isSpawned, listeners, config);
    }

    public MinionData withSkin(Optional<PropertyMap> skin) {
        return new MinionData(uuid, name, skin, isSpawned, listeners, config);
    }

    public MinionData withSpawned(boolean isSpawned) {
        return new MinionData(uuid, name, skin, isSpawned, listeners, config);
    }
}

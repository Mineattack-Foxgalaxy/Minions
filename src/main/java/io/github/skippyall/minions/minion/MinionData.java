package io.github.skippyall.minions.minion;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.listener.SerializableListenerManager;
import io.github.skippyall.minions.registration.MinionRegistries;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;
import java.util.UUID;

public class MinionData {
    public static final Codec<MinionData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    UUIDUtil.AUTHLIB_CODEC.fieldOf("uuid").forGetter(MinionData::getUuid),
                    Codec.STRING.fieldOf("name").forGetter(MinionData::getName),
                    ExtraCodecs.PROPERTY_MAP.optionalFieldOf("skin").forGetter(MinionData::getSkin),
                    Codec.BOOL.optionalFieldOf("isSpawned", false).forGetter(MinionData::isSpawned),
                    MinionConfig.CODEC.optionalFieldOf("config", new MinionConfig())
                            .forGetter(MinionData::getConfig),
                    SerializableListenerManager.getCodec(MinionRegistries.MINION_LISTENER_CODECS)
                            .optionalFieldOf("listeners").xmap(
                                    optional -> optional.orElseGet(SerializableListenerManager::new),
                                    Optional::of
                            ).forGetter(MinionData::getListeners)
            ).apply(instance, MinionData::new)
    );

    private UUID uuid;
    private String name;
    private Optional<PropertyMap> skin;
    private boolean isSpawned;
    private MinionConfig config;

    private final SerializableListenerManager<MinionListener> listeners;
    private Runnable onDirty;

    public MinionData(UUID uuid, String name, Optional<PropertyMap> skin, boolean isSpawned, MinionConfig config, SerializableListenerManager<MinionListener> listeners) {
        this(uuid, name, skin, isSpawned, config, listeners, () -> {});
    }

    public MinionData(UUID uuid, String name, Optional<PropertyMap> skin, boolean isSpawned, MinionConfig config, SerializableListenerManager<MinionListener> listeners, Runnable onDirty) {
        this.uuid = uuid;
        this.name = name;
        this.skin = skin;
        this.isSpawned = isSpawned;
        this.config = config;
        this.listeners = listeners;
        this.onDirty = onDirty;
    }

    public static MinionData createDefault(MinecraftServer server) {
        return new MinionData(
                UUID.randomUUID(),
                MinionProfileUtils.newDefaultMinionName(server),
                Optional.empty(),
                false,
                new MinionConfig(),
                new SerializableListenerManager<>()
        );
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
        onDirty.run();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        onDirty.run();
    }

    public Optional<PropertyMap> getSkin() {
        return skin;
    }

    public void setSkin(Optional<PropertyMap> skin) {
        this.skin = skin;
        onDirty.run();
    }

    public boolean isSpawned() {
        return isSpawned;
    }

    public void setSpawned(boolean spawned) {
        isSpawned = spawned;
        onDirty.run();
    }

    public MinionConfig getConfig() {
        return config;
    }

    public void setConfig(MinionConfig config) {
        this.config = config;
        onDirty.run();
    }

    public void setOnDirty(Runnable onDirty) {
        this.onDirty = onDirty;
    }

    public SerializableListenerManager<MinionListener> getListeners() {
        return listeners;
    }
}

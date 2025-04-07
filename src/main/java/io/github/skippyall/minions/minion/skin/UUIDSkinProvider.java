package io.github.skippyall.minions.minion.skin;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.minion.MinionProfileUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Uuids;
import net.minecraft.util.dynamic.Codecs;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UUIDSkinProvider extends CachedSkinProvider {
    public static final Codec<UUIDSkinProvider> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codecs.GAME_PROFILE_PROPERTY_MAP.optionalFieldOf("cache", null).forGetter(CachedSkinProvider::getCache),
                    Uuids.CODEC.fieldOf("uuid").forGetter(UUIDSkinProvider::getUuid)
            ).apply(instance, UUIDSkinProvider::new));

    private final UUID uuid;

    public UUIDSkinProvider(UUID uuid) {
        this(null, uuid);
    }

    public UUIDSkinProvider(PropertyMap cache, UUID uuid) {
        super(cache);
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public CompletableFuture<PropertyMap> fetchSkin(MinecraftServer server) {
        return MinionProfileUtils.getSkinOwnerProfile(server, uuid).thenApply(gameProfile -> {
            if (gameProfile != null) {
                return gameProfile.getProperties();
            } else {
                return null;
            }
        });
    }

    @Override
    public Codec<? extends SkinProvider> getCodec() {
        return CODEC;
    }
}

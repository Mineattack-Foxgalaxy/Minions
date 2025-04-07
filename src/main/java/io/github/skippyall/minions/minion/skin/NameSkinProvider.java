package io.github.skippyall.minions.minion.skin;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.minion.MinionProfileUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class NameSkinProvider extends CachedSkinProvider {
    public static final Codec<NameSkinProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(NameSkinProvider::getName),
            Codecs.GAME_PROFILE_PROPERTY_MAP.optionalFieldOf("cache", null).forGetter(NameSkinProvider::getCache)
    ).apply(instance, NameSkinProvider::new));

    private final String name;

    public NameSkinProvider(String name, @Nullable PropertyMap cache) {
        super(cache);
        this.name = name;
    }

    public NameSkinProvider(String name) {
        this(name, null);
    }

    public String getName() {
        return name;
    }

    @Override
    public CompletableFuture<PropertyMap> fetchSkin(MinecraftServer server) {
        return MinionProfileUtils.lookupSkinOwnerProfile(server, name).thenApply(gameProfile -> gameProfile != null ? gameProfile.getProperties() : null);
    }

    @Override
    public Codec<? extends SkinProvider> getCodec() {
        return null;
    }
}

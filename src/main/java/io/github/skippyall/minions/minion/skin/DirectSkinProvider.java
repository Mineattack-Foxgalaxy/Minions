package io.github.skippyall.minions.minion.skin;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.Codec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DirectSkinProvider implements SkinProvider {
    private final PropertyMap propertyMap;

    public static final Codec<DirectSkinProvider> CODEC = Codecs.GAME_PROFILE_PROPERTY_MAP.xmap(DirectSkinProvider::new, DirectSkinProvider::getPropertyMap);

    public DirectSkinProvider(PropertyMap propertyMap) {
        this.propertyMap = propertyMap;
    }

    public PropertyMap getPropertyMap() {
        return propertyMap;
    }

    @Override
    public CompletableFuture<@Nullable PropertyMap> getSkin(MinecraftServer server) {
        return CompletableFuture.completedFuture(propertyMap);
    }

    @Override
    public Codec<? extends SkinProvider> getCodec() {
        return null;
    }
}

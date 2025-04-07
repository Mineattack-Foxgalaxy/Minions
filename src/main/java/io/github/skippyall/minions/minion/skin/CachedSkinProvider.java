package io.github.skippyall.minions.minion.skin;

import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class CachedSkinProvider implements SkinProvider {
    private @Nullable PropertyMap cache = null;

    protected CachedSkinProvider(@Nullable PropertyMap cache) {
        this.cache = cache;
    }

    public abstract CompletableFuture<@Nullable PropertyMap> fetchSkin(MinecraftServer server);

    public @Nullable PropertyMap getCache() {
        return cache;
    }

    public CompletableFuture<@Nullable PropertyMap> updateCache(MinecraftServer server) {
        CompletableFuture<PropertyMap> future = fetchSkin(server);
        future.thenAccept(result -> {
            this.cache = result;
        });
        return future;
    }

    @Override
    public CompletableFuture<@Nullable PropertyMap> getSkin(MinecraftServer server) {
        CompletableFuture<PropertyMap> future = new CompletableFuture<>();
        if(cache == null) {
            fetchSkin(server).thenAccept(skin -> {
                cache = skin;
                future.complete(skin);
            });
        }
        return CompletableFuture.completedFuture(cache);
    }
}

package io.github.skippyall.minions.minion.skin;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.Codec;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface SkinProvider {
    CompletableFuture<@Nullable PropertyMap> getSkin(MinecraftServer server);

    Codec<? extends SkinProvider> getCodec();
}

package io.github.skippyall.minions.minion.skin;

import com.mojang.authlib.properties.PropertyMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public interface SkinProvider {
    CompletableFuture<Optional<PropertyMap>> openSkinMenu(ServerPlayer player);

    Component getDisplayName();
}

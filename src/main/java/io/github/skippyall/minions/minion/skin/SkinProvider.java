package io.github.skippyall.minions.minion.skin;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.concurrent.CompletableFuture;

public interface SkinProvider {
    CompletableFuture<ResolvableProfile> openSkinMenu(ServerPlayer player);

    Component getDisplayName();
}

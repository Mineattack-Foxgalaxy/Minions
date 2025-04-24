package io.github.skippyall.minions.minion.skin;

import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface SkinProvider {
    CompletableFuture<Optional<PropertyMap>> openSkinMenu(ServerPlayerEntity player);

    Text getDisplayName();
}

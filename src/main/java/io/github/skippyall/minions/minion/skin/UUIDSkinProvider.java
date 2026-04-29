package io.github.skippyall.minions.minion.skin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import io.github.skippyall.minions.gui.input.TextInput;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

public class UUIDSkinProvider implements SkinProvider {
    @Override
    public CompletableFuture<Optional<PropertyMap>> openSkinMenu(ServerPlayer player) {
        return TextInput.inputString(player, Component.translatable("minions.gui.look.skin.uuid.title"), "")
                .thenCompose(uuidString -> SkullBlockEntity.fetchGameProfile(UUID.fromString(uuidString)))
                .thenApply(gameProfile -> gameProfile.map(GameProfile::getProperties));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("minions.gui.look.skin.uuid");
    }
}

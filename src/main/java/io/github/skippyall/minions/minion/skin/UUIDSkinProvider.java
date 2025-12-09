package io.github.skippyall.minions.minion.skin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import io.github.skippyall.minions.gui.input.TextInput;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UUIDSkinProvider implements SkinProvider {
    @Override
    public CompletableFuture<Optional<PropertyMap>> openSkinMenu(ServerPlayerEntity player) {
        return TextInput.inputString(player, Text.translatable("minions.gui.look.skin.uuid.title"), "")
                .thenCompose(uuidString -> SkullBlockEntity.fetchProfileByUuid(UUID.fromString(uuidString)))
                .thenApply(gameProfile -> gameProfile.map(GameProfile::getProperties));
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("minions.gui.look.skin.uuid");
    }
}

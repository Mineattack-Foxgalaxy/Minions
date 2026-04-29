package io.github.skippyall.minions.minion.skin;

import io.github.skippyall.minions.gui.input.TextInput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.concurrent.CompletableFuture;

public class UUIDSkinProvider implements SkinProvider {
    @Override
    public CompletableFuture<ResolvableProfile> openSkinMenu(ServerPlayer player) {
        return TextInput.inputString(player, Component.translatable("minions.gui.look.skin.uuid.title"), "")
                .thenApply(ResolvableProfile::createUnresolved);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("minions.gui.look.skin.uuid");
    }
}

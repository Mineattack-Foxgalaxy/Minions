package io.github.skippyall.minions.minion.skin;

import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.input.TextInput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.concurrent.CompletableFuture;

public class UUIDSkinProvider implements SkinProvider {
    @Override
    public CompletableFuture<ResolvableProfile> openSkinMenu(MinionsGui parent) {
        return TextInput.inputStringFuture(parent, Component.translatable("minions.gui.look.skin.uuid.title"), "")
                .thenApply(name -> name != null ? ResolvableProfile.createUnresolved(name) : null);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("minions.gui.look.skin.uuid");
    }
}

package io.github.skippyall.minions.minion.skin;

import io.github.skippyall.minions.gui.MinionsGui;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface SkinProvider {
    CompletableFuture<@Nullable ResolvableProfile> openSkinMenu(MinionsGui parent);

    Component getDisplayName();
}

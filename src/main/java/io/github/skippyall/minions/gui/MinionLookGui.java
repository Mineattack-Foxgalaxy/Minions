package io.github.skippyall.minions.gui;

import com.mojang.authlib.GameProfile;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.gui.input.TextInput;
import io.github.skippyall.minions.minion.MinionData;
import io.github.skippyall.minions.minion.MinionItem;
import io.github.skippyall.minions.minion.MinionProfileUtils;
import io.github.skippyall.minions.minion.skin.SkinProvider;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.registration.SkinProviders;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MinionLookGui extends MinionsGui {
    private SimpleGui gui;

    private SkinProvider currentSkinProvider = SkinProviders.NAME;

    ItemStack minionItem;

    public MinionLookGui(
            ServerPlayer viewer,
            ItemStack minionItem
    ) {
        this.minionItem = minionItem;

        super(viewer);
        open();
    }

    public MinionData getData() {
        return MinionItem.getDataOrDefault(viewer.level().getServer(), minionItem);
    }

    @Override
    public void open() {
        gui = new SimpleGui(MenuType.GENERIC_9x3, viewer, false) {
            @Override
            public void onPlayerClose(boolean success) {
                onBackingClosed();
            }
        };
        update();
        gui.open();
    }

    @Override
    public void closeBacking() {
        gui.close();
    }

    public void update() {
        updateName();
        updateSkin();
        updateSkinProvider();
    }

    private void updateName() {
        gui.setSlot(10, new GuiElementBuilder()
                .setItem(Items.OAK_SIGN)
                .setName(Component.literal(this.getData().getName()))
                .setCallback(this::openRenameGui)
        );
    }

    private void updateSkin() {
        GuiElementBuilder builder = new GuiElementBuilder()
                .setItem(Items.PLAYER_HEAD)
                .setCallback(this::openSkinGui);

        if(MinionItem.containsData(minionItem)) {
            MinionData data = getData();
            if (data.getSkin().isPresent()) {
                builder.setComponent(
                        DataComponents.PROFILE,
                        ResolvableProfile.createResolved(new GameProfile(FakePlayer.DEFAULT_UUID, "", data.getSkin().get()))
                );
            }
        }

        gui.setSlot(16, builder);
    }

    private void updateSkinProvider() {
        gui.setSlot(25, new GuiElementBuilder()
                .setItem(Items.GREEN_STAINED_GLASS_PANE)
                .setComponent(DataComponents.CUSTOM_NAME, currentSkinProvider.getDisplayName())
                .setCallback(this::cycleSkinProvider)
        );
    }

    public void openSkinGui() {
        currentSkinProvider.openSkinMenu(this)
                .<@Nullable GameProfile>thenCompose(profile -> {
                    if(profile != null) {
                        return profile.resolveProfile(viewer.level().getServer().services().profileResolver());
                    } else {
                        return CompletableFuture.completedFuture(null);
                    }
                })
                .thenAccept(skin -> {
                    if(skin != null) {
                        getData().setSkin(Optional.of(skin.properties()));
                        updateSkin();
                    }
                });
    }

    private void cycleSkinProvider() {
        var currentId = MinionRegistries.SKIN_PROVIDERS.getId(currentSkinProvider);
        currentId++;
        if (MinionRegistries.SKIN_PROVIDERS.size() == currentId) {
            currentId = 0;
        }

        currentSkinProvider = Objects.requireNonNull(MinionRegistries.SKIN_PROVIDERS.byId(currentId));
        updateSkinProvider();
    }

    public void openRenameGui() {
        TextInput.input(
                this,
                Component.translatable("minions.gui.look.rename.title"),
                "Minion",
                name -> CompletableFuture.completedFuture(MinionProfileUtils.checkMinionNameWithoutPrefix(viewer.level().getServer(), name))
        ).thenAccept(newName -> {
            if(newName != null) {
                getData().setName(newName);
                updateName();
            }
        });
    }
}


package io.github.skippyall.minions.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.gui.input.TextInput;
import io.github.skippyall.minions.minion.MinionData;
import io.github.skippyall.minions.minion.MinionItem;
import io.github.skippyall.minions.minion.MinionProfileUtils;
import io.github.skippyall.minions.minion.skin.SkinProvider;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.registration.SkinProviders;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;

public class MinionLookGui extends SimpleGui {
    private ItemStack minionItem;
    private SkinProvider currentSkinProvider;

    public MinionLookGui(ServerPlayer player, ItemStack minionItem) {
        super(MenuType.GENERIC_9x3, player, false);
        this.minionItem = minionItem;
        this.currentSkinProvider = SkinProviders.NAME;
    }

    public void update() {
        updateName();
        updateSkin();
        updateSkinProvider();
    }

    private void updateName() {
        setSlot(10, new GuiElementBuilder()
                .setItem(Items.OAK_SIGN)
                .setName(Component.literal(getData().name()))
                .setCallback(() -> {
                    openRenameGui(player, minionItem);
                })
        );
    }

    private void updateSkin() {
        GuiElementBuilder builder = new GuiElementBuilder()
                .setItem(Items.PLAYER_HEAD)
                .setCallback(() -> currentSkinProvider.openSkinMenu(player).thenAccept(skin -> {
                    MinionItem.setData(player.getServer(), getData().withSkin(skin), minionItem);
                }));
        if(MinionItem.getData(player.getServer(), minionItem) != null && MinionItem.getData(player.getServer(), minionItem).skin().isPresent()) {
            builder.setComponent(DataComponents.PROFILE, new ResolvableProfile(Optional.empty(), Optional.empty(), getData().skin().get()));
        }
        setSlot(16, builder);
    }

    private void cycleSkinProvider() {
        int currentId = MinionRegistries.SKIN_PROVIDERS.getId(currentSkinProvider);
        currentId++;
        if(MinionRegistries.SKIN_PROVIDERS.size() == currentId) {
            currentId = 0;
        }

        currentSkinProvider = MinionRegistries.SKIN_PROVIDERS.byId(currentId);
        updateSkinProvider();
    }

    private void updateSkinProvider() {
        setSlot(25, new GuiElementBuilder()
                .setItem(Items.GREEN_STAINED_GLASS_PANE)
                .setComponent(DataComponents.CUSTOM_NAME, currentSkinProvider.getDisplayName())
                .setCallback(this::cycleSkinProvider)
        );
    }

    private MinionData getData() {
        return MinionItem.getDataOrDefault(player.getServer(), minionItem);
    }

    public static void open(ServerPlayer player, ItemStack minionItem) {
        MinionLookGui gui = new MinionLookGui(player, minionItem);
        gui.update();
        gui.open();
    }

    public void openRenameGui(ServerPlayer player, ItemStack minionItem) {
        TextInput.inputSync(player, Component.translatable("minions.gui.look.rename.title"), "Minion", name -> MinionProfileUtils.checkMinionNameWithoutPrefix(player.getServer(), name))
                .thenAccept(name -> {
                    MinionItem.setData(player.getServer(), getData().withName(MinionProfileUtils.getPrefix() + name), minionItem);
                    open();
                });
    }
}

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
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Optional;

public class MinionLookGui extends SimpleGui {
    private ItemStack minionItem;
    private SkinProvider currentSkinProvider;

    public MinionLookGui(ServerPlayerEntity player, ItemStack minionItem) {
        super(ScreenHandlerType.GENERIC_9X3, player, false);
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
                .setName(Text.literal(getData().name()))
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
            builder.setComponent(DataComponentTypes.PROFILE, new ProfileComponent(Optional.empty(), Optional.empty(), getData().skin().get()));
        }
        setSlot(16, builder);
    }

    private void cycleSkinProvider() {
        int currentId = MinionRegistries.SKIN_PROVIDERS.getRawId(currentSkinProvider);
        currentId++;
        if(MinionRegistries.SKIN_PROVIDERS.size() == currentId) {
            currentId = 0;
        }

        currentSkinProvider = MinionRegistries.SKIN_PROVIDERS.get(currentId);
        updateSkinProvider();
    }

    private void updateSkinProvider() {
        setSlot(25, new GuiElementBuilder()
                .setItem(Items.GREEN_STAINED_GLASS_PANE)
                .setComponent(DataComponentTypes.CUSTOM_NAME, currentSkinProvider.getDisplayName())
                .setCallback(this::cycleSkinProvider)
        );
    }

    private MinionData getData() {
        return MinionItem.getDataOrDefault(player.getServer(), minionItem);
    }

    public static void open(ServerPlayerEntity player, ItemStack minionItem) {
        MinionLookGui gui = new MinionLookGui(player, minionItem);
        gui.update();
        gui.open();
    }

    public void openRenameGui(ServerPlayerEntity player, ItemStack minionItem) {
        TextInput.inputSync(player, Text.translatable("minions.gui.look.rename.title"), "Minion", name -> MinionProfileUtils.checkMinionNameWithoutPrefix(player.getServer(), name))
                .thenAccept(name -> {
                    MinionItem.setData(player.getServer(), getData().withName(MinionProfileUtils.getPrefix() + name), minionItem);
                    open();
                });
    }
}

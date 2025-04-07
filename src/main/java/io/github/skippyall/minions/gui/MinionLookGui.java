package io.github.skippyall.minions.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.input.TextInput;
import io.github.skippyall.minions.minion.MinionData;
import io.github.skippyall.minions.minion.MinionItem;
import io.github.skippyall.minions.minion.MinionProfileUtils;
import net.minecraft.block.PlayerSkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
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

    public MinionLookGui(ServerPlayerEntity player, ItemStack minionItem) {
        super(ScreenHandlerType.GENERIC_9X3, player, false);
        this.minionItem = minionItem;
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
        setSlot(16, new GuiElementBuilder()
                .setItem(Items.PLAYER_HEAD)
        );

        getData().getSkin(player.server).thenAccept(skin -> {
            setSlot(16, new GuiElementBuilder()
                    .setItem(Items.PLAYER_HEAD)
                    .setComponent(DataComponentTypes.PROFILE, new ProfileComponent(Optional.empty(), Optional.empty(), skin))
                    .setCallback(this::cycleSkinProvider)
            );
        });
    }

    private void cycleSkinProvider() {

    }

    private void updateSkinProvider() {
        setSlot(25, new GuiElementBuilder()
                .setItem(Items.GREEN_STAINED_GLASS_PANE)
                .setComponent(DataComponentTypes.CUSTOM_NAME, Text.literal())
        );
        updateSkin();
    }

    private MinionData getData() {
        return MinionItem.getData(minionItem);
    }

    public static void open(ServerPlayerEntity player, ItemStack minionItem) {
        MinionLookGui gui = new MinionLookGui(player, minionItem);

        gui.open();
    }

    public void openRenameGui(ServerPlayerEntity player, ItemStack minionItem) {
        TextInput.input(player, Text.translatable("minions.gui.look.rename.title"), "Minion", MinionProfileUtils::checkMinionName)
                .thenAccept(name -> {
                    MinionItem.setData(MinionItem.getDataOrDefault(minionItem).withName(name), minionItem);
                    open();
                });
    }
}

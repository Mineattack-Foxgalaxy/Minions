package io.github.skippyall.minions;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MinionInventory {
    public static void openInventory(ServerPlayer player, MinionFakePlayer minion) {

    }

    public static void openServerSideInventory(ServerPlayer player, MinionFakePlayer minion) {
        SimpleGui gui = new SimpleGui(MenuType.GENERIC_3x3, player, false);
        gui.setTitle(Component.literal("Minion"));
        gui.setSlot(4, new GuiElementBuilder()
                .setItem(Items.REDSTONE)
                .setName(Component.literal("Programming"))
                .setCallback((i, clickType, clickType1) -> {
                    openProgrammingInventory(player, minion);
                })
        );
        gui.setSlot(3, new GuiElementBuilder()
                .setItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
                .setName(Component.literal("Modules and Detectors"))
                .setCallback((i, clickType, clickType1) -> {

                })
        );
        gui.open();
    }

    public static void openProgrammingInventory(ServerPlayer player, MinionFakePlayer minion) {

    }

    public static void open
}

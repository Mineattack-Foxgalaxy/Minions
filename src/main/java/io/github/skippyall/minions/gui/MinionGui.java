package io.github.skippyall.minions.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class MinionGui {
    public static void openInventory(ServerPlayerEntity player, MinionFakePlayer minion) {
        openServerSideInventory(player, minion);
    }

    public static void openServerSideInventory(ServerPlayerEntity player, MinionFakePlayer minion) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_3X3, player, false);
        gui.setTitle(minion.getName());

        gui.setSlot(1, new GuiElementBuilder()
                .setItem(Items.COMMAND_BLOCK)
                .setName(Text.translatable("minions.gui.main.commands"))
                .setCallback((i, clickType, slotActionType) -> {
                    openCommandsGui(player, minion);
                })
        );
        if(minion.isProgrammable()) {
            gui.setSlot(4, new GuiElementBuilder()
                    .setItem(Items.REDSTONE)
                    .setName(Text.translatable("minions.gui.main.programming"))
                    .setCallback((i, clickType, slotActionType) -> {
                        openProgrammingInventory(player, minion);
                    })
            );
        }
        gui.setSlot(3, new GuiElementBuilder()
                .setItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
                .setName(Text.translatable("minions.gui.main.modules"))
                .setCallback((i, clickType, slotActionType) -> {
                    ModuleInventory.openModuleInventory(player, minion);
                })
        );
        gui.setSlot(5, new GuiElementBuilder()
                .setItem(Items.CHEST)
                .setName(Text.translatable("minions.gui.main.inventory"))
                .setCallback((i, clickType, slotActionType) -> {
                    openMinionInventory(player, minion);
                })
        );
        gui.open();
    }

    public static void openCommandsGui(ServerPlayerEntity player, MinionFakePlayer minion) {
        CommandsGui.openServerModuleCommandGui(player, minion);
    }

    public static void openProgrammingInventory(ServerPlayerEntity player, MinionFakePlayer minion) {

    }

    public static void openMinionInventory(ServerPlayerEntity player, MinionFakePlayer minion) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X5, player, false);
        gui.setTitle(Text.translatable("minions.gui.inventory.title", minion.getName()));
        for (int i = 0; i < minion.getInventory().size(); i++) {
            gui.setSlotRedirect(i, new Slot(minion.getInventory(), i, 0, 0));
        }
        gui.open();
    }
}

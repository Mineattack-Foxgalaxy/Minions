package io.github.skippyall.minions.minion;

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

public class MinionInventory {
    public static void openInventory(ServerPlayerEntity player, MinionFakePlayer minion) {
        openServerSideInventory(player, minion);
    }

    public static void openServerSideInventory(ServerPlayerEntity player, MinionFakePlayer minion) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_3X3, player, false);
        gui.setTitle(Text.literal("Minion"));

        gui.setSlot(1, new GuiElementBuilder()
                .setItem(Items.COMMAND_BLOCK)
                .setName(Text.literal("Commands"))
                .setCallback((i, clickType, slotActionType) -> {
                    openCommandsGui(player, minion);
                })
        );
        if(minion.isProgrammable()) {
            gui.setSlot(4, new GuiElementBuilder()
                    .setItem(Items.REDSTONE)
                    .setName(Text.literal("Programming"))
                    .setCallback((i, clickType, slotActionType) -> {
                        openProgrammingInventory(player, minion);
                    })
            );
        }
        gui.setSlot(3, new GuiElementBuilder()
                .setItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
                .setName(Text.literal("Modules and Detectors"))
                .setCallback((i, clickType, slotActionType) -> {
                    openModuleInventory(player, minion);
                })
        );
        gui.setSlot(5, new GuiElementBuilder()
                .setItem(Items.CHEST)
                .setName(Text.literal("Inventory"))
                .setCallback((i, clickType, slotActionType) -> {
                    openMinionInventory(player, minion);
                })
        );
        gui.open();
    }

    public static void openCommandsGui(ServerPlayerEntity player, MinionFakePlayer minion) {
        CommandsInventory.openServerCommandsInventory(player, minion);
    }

    public static void openProgrammingInventory(ServerPlayerEntity player, MinionFakePlayer minion) {

    }

    public static void openModuleInventory(ServerPlayerEntity player, MinionFakePlayer minion) {
        player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, playerInventory, player2) -> GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, minion.getModuleInventory()), Text.literal("")));
    }

    public static void openMinionInventory(ServerPlayerEntity player, MinionFakePlayer minion) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X5, player, false);
        gui.setTitle(Text.literal("Minion"));
        for (int i = 0; i < minion.getInventory().size(); i++) {
            gui.setSlotRedirect(i, new Slot(minion.getInventory(), i, 0, 0));
        }
        gui.open();
    }
}

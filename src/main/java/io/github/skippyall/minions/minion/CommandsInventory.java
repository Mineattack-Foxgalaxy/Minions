package io.github.skippyall.minions.minion;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class CommandsInventory {
    public static void openServerCommandsInventory(ServerPlayerEntity player, MinionFakePlayer minion) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false);

        gui.setSlot(0, new GuiElementBuilder()
                .setItem(Items.MINECART)
                .setName(Text.literal("Get into minecart"))
                .setCallback(() -> {
                    minion.getMinionActionPack().mount(true);
                })
        );

        gui.open();
    }
}

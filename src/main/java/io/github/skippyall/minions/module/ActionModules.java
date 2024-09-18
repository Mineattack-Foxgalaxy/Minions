package io.github.skippyall.minions.module;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.command.CommandExecutor;
import io.github.skippyall.minions.fakeplayer.EntityPlayerActionPack;
import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ActionModules {
    public static void executeOnce(EntityPlayerActionPack.ActionType actionType, ServerPlayerEntity player, MinionFakePlayer minion) {
        minion.getMinionActionPack().start(actionType, EntityPlayerActionPack.Action.once());
    }

    public static void intervalExecutor(EntityPlayerActionPack.ActionType actionType, ServerPlayerEntity player, MinionFakePlayer minion) {
        minion.getMinionActionPack().start(actionType, EntityPlayerActionPack.Action.interval());
    }

    public static CommandExecutor detailSelectionExecutor(EntityPlayerActionPack.ActionType actionType, Text actionName) {
        return (player, minion) -> {
            SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_3X3, player, false);
            gui.setTitle(Text.translatable("minions.command.action.details", actionName));

            gui.setSlot(1, new GuiElementBuilder()
                    .setItem(Items.COMMAND_BLOCK)
                    .setName(Text.translatable("minions.command.action.once", actionName))
                    .setCallback(() -> executeOnce(actionType, player, minion))
            );
        };
    }
}

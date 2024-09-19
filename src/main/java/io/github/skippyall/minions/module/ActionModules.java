package io.github.skippyall.minions.module;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.command.CommandExecutor;
import io.github.skippyall.minions.fakeplayer.EntityPlayerActionPack;
import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.input.TextInput;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ActionModules {
    public static void executeOnce(EntityPlayerActionPack.ActionType actionType, ServerPlayerEntity player, MinionFakePlayer minion) {
        minion.getMinionActionPack().start(actionType, EntityPlayerActionPack.Action.once());
    }

    public static void executeInterval(EntityPlayerActionPack.ActionType actionType, ServerPlayerEntity player, MinionFakePlayer minion) {
        TextInput.inputText(player, Text.translatable("minions.command.action.interval.enter_interval"), "1")
                .thenAccept(string -> {
                    try {
                        int ticks = Integer.parseInt(string);
                        minion.getMinionActionPack().start(actionType, EntityPlayerActionPack.Action.interval(ticks));
                    } catch (NumberFormatException ignored) {}
                });
    }

    public static void executeContinuous(EntityPlayerActionPack.ActionType actionType, ServerPlayerEntity player, MinionFakePlayer minion) {
        minion.getMinionActionPack().start(actionType, EntityPlayerActionPack.Action.continuous());
    }

    public static void stop(EntityPlayerActionPack.ActionType actionType, ServerPlayerEntity player, MinionFakePlayer minion) {
        minion.getMinionActionPack().stop(actionType);
    }

    public static CommandExecutor detailSelectionExecutor(EntityPlayerActionPack.ActionType actionType, Text actionName) {
        return (player, minion) -> {
            SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_3X3, player, false);
            gui.setTitle(Text.translatable("minions.command.action.details", actionName));

            gui.setSlot(3, new GuiElementBuilder()
                    .setItem(Items.COMMAND_BLOCK)
                    .setName(Text.translatable("minions.command.action.once", actionName))
                    .setCallback(() -> executeOnce(actionType, player, minion))
            );
            gui.setSlot(4, new GuiElementBuilder()
                    .setItem(Items.CHAIN_COMMAND_BLOCK)
                    .setName(Text.translatable("minions.command.action.interval", actionName))
                    .setCallback(() -> executeInterval(actionType, player, minion))
            );
            gui.setSlot(5, new GuiElementBuilder()
                    .setItem(Items.REPEATING_COMMAND_BLOCK)
                    .setName(Text.translatable("minions.command.action.continuous", actionName))
                    .setCallback(() -> executeContinuous())
            );
            gui.setSlot(7, new GuiElementBuilder()
                    .setItem(Items.BARRIER)
                    .setName(Text.translatable("minions.command.action.stop", actionName))
                    .setCallback(() -> stop(actionType, player, minion))
            );
        };
    }
}

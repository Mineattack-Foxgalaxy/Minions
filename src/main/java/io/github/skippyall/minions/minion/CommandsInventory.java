package io.github.skippyall.minions.minion;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.command.Command;
import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class CommandsInventory {
    public static void openServerCommandsInventory(ServerPlayerEntity player, MinionFakePlayer minion) {
        List<Command> commands = minion.getModuleInventory().getAllCommands();
        int rows = (int) Math.ceil((double) commands.size() / 9.0);

        if(rows != 0) {
            boolean paged = false;
            SimpleGui gui = new SimpleGui(getTypeForRows(rows), player, false);

            for (int i = 0; i < commands.size() && i < 54; i++) {
                Command command = commands.get(i);
                gui.setSlot(i, new GuiElementBuilder()
                        .setItem(command.getItemRepresentation())
                        .setName(command.getName())
                        .addLoreLine(command.getDescription())
                        .setCallback(() -> command.onRun(player, minion))
                );
            }

            gui.open();
        }
    }

    public static ScreenHandlerType<GenericContainerScreenHandler> getTypeForRows(int rows) {
        return switch (rows) {
            case 1 -> ScreenHandlerType.GENERIC_9X1;
            case 2 -> ScreenHandlerType.GENERIC_9X2;
            case 3 -> ScreenHandlerType.GENERIC_9X3;
            case 4 -> ScreenHandlerType.GENERIC_9X4;
            case 5 -> ScreenHandlerType.GENERIC_9X5;
            case 6 -> ScreenHandlerType.GENERIC_9X6;
            default -> throw new IllegalStateException("Unexpected value: " + rows);
        };
    }
}

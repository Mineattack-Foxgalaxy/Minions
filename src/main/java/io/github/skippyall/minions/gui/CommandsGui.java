package io.github.skippyall.minions.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.command.Command;
import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.module.ModuleItem;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

public class CommandsGui {
    public static void openServerModuleCommandGui(ServerPlayerEntity player, MinionFakePlayer minion) {
        List<ModuleItem> modules = minion.getModuleInventory().getModuleItems();

        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false);

        gui.setTitle(Text.translatable("minions.gui.module_commands.title"));

        for (int i = 0; i < modules.size(); i++) {
            ModuleItem module = modules.get(i);
            gui.setSlot(i, new GuiElementBuilder()
                    .setItem(module.asItem())
                    .setCallback(() -> openServerCommandGui(player, minion, module))
            );
        }

        gui.open();
    }

    public static void openServerCommandGui(ServerPlayerEntity player, MinionFakePlayer minion, ModuleItem module) {
        List<Command> commands = module.getCommands();

        SimpleGui commandGui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false);

        commandGui.setTitle(Text.translatable("minions.gui.commands.title", module.asItem().getName()));

        for(int j = 0; j < commands.size(); j++) {
            Command command = commands.get(j);
            commandGui.setSlot(j, new GuiElementBuilder()
                    .setItem(command.getItemRepresentation())
                    .setName(command.getName())
                    .addLoreLine(command.getDescription())
                    .setCallback(() -> command.execute(player, minion))
            );
        }

        commandGui.open();
    }
}

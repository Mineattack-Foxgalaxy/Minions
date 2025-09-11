package io.github.skippyall.minions.gui;

public class CommandsGui {
    /*public static void openServerModuleCommandGui(ServerPlayerEntity player, MinionFakePlayer minion) {
        Collection<ModuleItem> modules = minion.getModuleInventory().getModules();

        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false);

        gui.setTitle(Text.translatable("minions.gui.module_commands.title"));

        for (ModuleItem module : modules) {
            gui.addSlot(new GuiElementBuilder()
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
    }*/
}

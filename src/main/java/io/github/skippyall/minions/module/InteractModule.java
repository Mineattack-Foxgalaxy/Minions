package io.github.skippyall.minions.module;

import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.module.action.ActionModules;
import io.github.skippyall.minions.module.command.SimpleCommand;
import io.github.skippyall.minions.minion.fakeplayer.EntityPlayerActionPack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

import static io.github.skippyall.minions.module.Modules.register;

public class InteractModule {
    public static final SimpleCommand INTERACT_COMMAND = new SimpleCommand(
            Text.translatable("minions.command.interact.name"),
            Text.translatable("minions.command.interact.description"),
            Items.LEVER,
            ActionModules.detailSelectionExecutor(EntityPlayerActionPack.ActionType.USE, Text.translatable("minions.command.interact.name"))
    );

    public static final SimpleModuleItem INTERACT_MODULE = register(
            Identifier.of(Minions.MOD_ID, "interact_module"),
            List.of(),
            List.of(INTERACT_COMMAND),
            Items.LEVER
    );

    public static void registerMe() {

    }
}


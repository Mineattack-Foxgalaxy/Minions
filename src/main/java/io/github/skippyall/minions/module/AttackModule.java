package io.github.skippyall.minions.module;

import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.command.SimpleCommand;
import io.github.skippyall.minions.minion.fakeplayer.EntityPlayerActionPack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

import static io.github.skippyall.minions.module.Modules.register;

public class AttackModule {
    public static final SimpleCommand ATTACK_COMMAND = new SimpleCommand(
            Text.translatable("minions.command.attack.name"),
            Text.translatable("minions.command.attack.description"),
            Items.DIAMOND_PICKAXE,
            ActionModules.detailSelectionExecutor(EntityPlayerActionPack.ActionType.ATTACK, Text.translatable("minions.command.attack.name"))
    );

    public static final SimpleModuleItem ATTACK_MODULE = register(
            Identifier.of(Minions.MOD_ID, "attack_module"),
            List.of(),
            List.of(ATTACK_COMMAND),
            Items.DIAMOND_PICKAXE
    );

    public static void registerMe() {

    }
}


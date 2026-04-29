package io.github.skippyall.minions.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.skippyall.minions.MinionsConfig;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import static net.minecraft.commands.Commands.literal;

public class MinionsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext access, Commands.CommandSelection environment) {
        LiteralArgumentBuilder<CommandSourceStack> builder = literal("minions")
                .then(SpawnSubcommand.SPAWN)
                .then(ListSubcommand.LIST)
                .then(DocsSubcommand.DOCS)
                .then(TestSubcommand.TEST);

        if(MinionsConfig.get().minion.enableMobCapHacks) {
            builder.then(MobCapDebugSubcommand.MOB_CAP_DEBUG);
        }

        dispatcher.register(
                builder
        );
    }
}

package io.github.skippyall.minions.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.skippyall.minions.MinionsConfig;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class MinionsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        LiteralArgumentBuilder<ServerCommandSource> builder = literal("minions")
                .then(SpawnSubcommand.SPAWN)
                .then(ListSubcommand.LIST)
                .then(DocsSubcommand.DOCS);

        if(MinionsConfig.get().minion.enableMobCapHacks) {
            builder.then(MobCapDebugSubcommand.MOB_CAP_DEBUG);
        }

        dispatcher.register(
                builder
        );
    }
}

package io.github.skippyall.minions.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class MinionsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("minions")
                        .then(SpawnSubcommand.SPAWN)
                        .then(MobCapDebugSubcommand.MOB_CAP_DEBUG)
        );
    }


}

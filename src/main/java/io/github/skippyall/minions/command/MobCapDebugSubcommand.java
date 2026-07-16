package io.github.skippyall.minions.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.skippyall.minions.mixinhelper.antimobcap.ChunkLevelManager$DistanceFromNearestPlayerTrackerAccessor;
import io.github.skippyall.minions.mixinhelper.antimobcap.ChunkLevelManagerAccessor;
import io.github.skippyall.minions.mixins.mobcap.ServerChunkCacheAccessor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.DistanceManager;

import static net.minecraft.commands.Commands.literal;

public class MobCapDebugSubcommand {
    public static final LiteralArgumentBuilder<CommandSourceStack> MOB_CAP_DEBUG = literal("mobcapdebug")
            .executes(MobCapDebugSubcommand::mobcapdebugCommand);

    public static int mobcapdebugCommand(CommandContext<CommandSourceStack> context) {
        DistanceManager levelManager = ((ServerChunkCacheAccessor)context.getSource().getLevel().getChunkSource()).getDistanceManager();
        int tickedChunkCount = ((ChunkLevelManager$DistanceFromNearestPlayerTrackerAccessor)((ChunkLevelManagerAccessor)levelManager).minions$getMinionless()).minions$getTickedChunkCount();
        context.getSource().sendSuccess(() -> Component.nullToEmpty(String.valueOf(tickedChunkCount)), false);
        return 0;
    }
}

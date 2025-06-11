package io.github.skippyall.minions.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.skippyall.minions.mixinhelper.ChunkLevelManager$DistanceFromNearestPlayerTrackerAccessor;
import io.github.skippyall.minions.mixinhelper.ChunkLevelManagerAccessor;
import io.github.skippyall.minions.mixins.antimobcap.ServerChunkManagerAccessor;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ChunkLevelManager;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class MobCapDebugSubcommand {
    public static final LiteralArgumentBuilder<ServerCommandSource> MOB_CAP_DEBUG = literal("mobcapdebug")
            .executes(MobCapDebugSubcommand::mobcapdebugCommand);

    public static int mobcapdebugCommand(CommandContext<ServerCommandSource> context) {
        ChunkLevelManager levelManager = ((ServerChunkManagerAccessor)context.getSource().getWorld().getChunkManager()).getLevelManager();
        int tickedChunkCount = ((ChunkLevelManager$DistanceFromNearestPlayerTrackerAccessor)((ChunkLevelManagerAccessor)levelManager).minions$getMinionless()).minions$getTickedChunkCount();
        context.getSource().sendFeedback(() -> Text.of(String.valueOf(tickedChunkCount)), false);
        return 0;
    }
}

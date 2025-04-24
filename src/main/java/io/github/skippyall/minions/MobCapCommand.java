package io.github.skippyall.minions;

import com.mojang.brigadier.CommandDispatcher;
import io.github.skippyall.minions.mixinhelper.ChunkTicketManager$DistanceFromNearestPlayerTrackerAccessor;
import io.github.skippyall.minions.mixinhelper.ChunkTicketManagerAccessor;
import io.github.skippyall.minions.mixins.antimobcap.ServerChunkManagerAccessor;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class MobCapCommand {
    public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("mobcapdebug")
                .executes(context -> {
                    ChunkTicketManager ticketManager = ((ServerChunkManagerAccessor)context.getSource().getWorld().getChunkManager()).getTicketManager();
                    int tickedChunkCount = ((ChunkTicketManager$DistanceFromNearestPlayerTrackerAccessor)((ChunkTicketManagerAccessor)ticketManager).getMinionless()).minions$getTickedChunkCount();
                    context.getSource().sendFeedback(() -> Text.of(String.valueOf(tickedChunkCount)), false);
                    return 0;
                })
        );
    }
}

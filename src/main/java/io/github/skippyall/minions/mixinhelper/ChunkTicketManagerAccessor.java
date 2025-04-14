package io.github.skippyall.minions.mixinhelper;

import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketManager;

public interface ChunkTicketManagerAccessor {
    ObjectSet<ServerPlayerEntity> getPlayers(long chunkpos);

    ChunkTicketManager.DistanceFromNearestPlayerTracker getMinionless();
}

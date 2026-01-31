package io.github.skippyall.minions.mixinhelper.antimobcap;

import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkLevelManager;

public interface ChunkLevelManagerAccessor {
    ObjectSet<ServerPlayerEntity> minions$getPlayers(long chunkpos);

    ChunkLevelManager.DistanceFromNearestPlayerTracker minions$getMinionless();
}

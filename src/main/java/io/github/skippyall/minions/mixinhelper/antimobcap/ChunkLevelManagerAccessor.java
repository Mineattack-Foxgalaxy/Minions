package io.github.skippyall.minions.mixinhelper.antimobcap;

import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerPlayer;

public interface ChunkLevelManagerAccessor {
    ObjectSet<ServerPlayer> minions$getPlayers(long chunkpos);

    DistanceManager.FixedPlayerDistanceChunkTracker minions$getMinionless();
}

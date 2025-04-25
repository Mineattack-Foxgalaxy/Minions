package io.github.skippyall.minions.mixinhelper;

public interface ChunkLevelManager$DistanceFromNearestPlayerTrackerAccessor {
    void minions$markAsMinionless();
    void minions$markAsTarget();
    int minions$getTickedChunkCount();

    boolean minions$isRealPlayerInChunk(long chunkPos);
}

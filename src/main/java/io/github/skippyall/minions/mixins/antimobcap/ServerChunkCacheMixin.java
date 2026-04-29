package io.github.skippyall.minions.mixins.antimobcap;

import io.github.skippyall.minions.mixinhelper.antimobcap.ChunkLevelManager$DistanceFromNearestPlayerTrackerAccessor;
import io.github.skippyall.minions.mixinhelper.antimobcap.ChunkLevelManagerAccessor;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerChunkCache;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin {
    @Shadow
    @Final
    private DistanceManager distanceManager;

    @ModifyArg(method = "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;J)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/NaturalSpawner;createState(ILjava/lang/Iterable;Lnet/minecraft/world/level/NaturalSpawner$ChunkGetter;Lnet/minecraft/world/level/LocalMobCapCalculator;)Lnet/minecraft/world/level/NaturalSpawner$SpawnState;"))
    public int useMinionless(int spawningChunkCount) {
        return ((ChunkLevelManager$DistanceFromNearestPlayerTrackerAccessor)((ChunkLevelManagerAccessor)distanceManager).minions$getMinionless()).minions$getTickedChunkCount();
    }
}

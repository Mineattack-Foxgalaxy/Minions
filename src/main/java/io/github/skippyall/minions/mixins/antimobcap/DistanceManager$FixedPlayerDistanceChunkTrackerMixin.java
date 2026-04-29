package io.github.skippyall.minions.mixins.antimobcap;

import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.mixinhelper.antimobcap.ChunkLevelManager$DistanceFromNearestPlayerTrackerAccessor;
import io.github.skippyall.minions.mixinhelper.antimobcap.ChunkLevelManagerAccessor;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DistanceManager.FixedPlayerDistanceChunkTracker.class)
public abstract class DistanceManager$FixedPlayerDistanceChunkTrackerMixin extends ChunkTrackerMixin implements ChunkLevelManager$DistanceFromNearestPlayerTrackerAccessor {
    @Final
    @Shadow
    DistanceManager field_17462;

    @Shadow
    @Final
    protected Long2ByteMap chunks;

    @Shadow protected abstract boolean havePlayer(long chunkPos);

    @Unique
    boolean minions$minionless, minions$target;

    @Inject(method = "havePlayer", at = @At("RETURN"), cancellable = true)
    public void minions$filterMinions(long chunkPos, CallbackInfoReturnable<Boolean> cir) {
        if (minions$minionless) {
            cir.setReturnValue(minions$isRealPlayerInChunk(chunkPos));
        }
    }

    @Override
    public boolean minions$isRealPlayerInChunk(long chunkPos) {
        ObjectSet<ServerPlayer> players = ((ChunkLevelManagerAccessor)field_17462).minions$getPlayers(chunkPos);
        boolean contains = false;
        if(players != null) {
            contains = players.stream().anyMatch(player -> {
                if (player instanceof MinionFakePlayer minion) {
                    return minion.canSpawnMobs();
                }
                return true;
            });
        }
        return contains;
    }

    @Inject(method = "runAllUpdates", at = @At("HEAD"))
    public void minions$sync(CallbackInfo info) {
        if (minions$target) {
            ((ChunkLevelManagerAccessor)field_17462).minions$getMinionless().runAllUpdates();
        }
    }

    @Override
    public void minions$updateLevel(long chunkPos, int distance, boolean decrease, CallbackInfo info) {
        if (minions$target && (distance == Integer.MAX_VALUE || minions$isRealPlayerInChunk(chunkPos))) {
            ((ChunkLevelManagerAccessor)field_17462).minions$getMinionless().update(chunkPos, distance, decrease);
        }
    }

    @Override
    public void minions$markAsMinionless() {
        minions$minionless = true;
    }

    @Override
    public void minions$markAsTarget() {
        minions$target = true;
    }

    @Override
    public int minions$getTickedChunkCount() {
        return chunks.size();
    }
}

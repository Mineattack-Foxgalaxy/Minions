package io.github.skippyall.minions.mixins.antimobcap;

import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.mixinhelper.ChunkTicketManager$DistanceFromNearestPlayerTrackerAccessor;
import io.github.skippyall.minions.mixinhelper.ChunkTicketManagerAccessor;
import io.github.skippyall.minions.module.MobSpawningModule;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChunkTicketManager.DistanceFromNearestPlayerTracker.class)
public abstract class ChunkTicketManager$DistanceFromNearestPlayerTrackerMixin extends ChunkPosDistanceLevelPropagatorMixin implements ChunkTicketManager$DistanceFromNearestPlayerTrackerAccessor {
    @Final
    @Shadow
    ChunkTicketManager field_17462;

    @Shadow
    @Final
    protected Long2ByteMap distanceFromNearestPlayer;

    @Shadow protected abstract boolean isPlayerInChunk(long chunkPos);

    @Unique
    boolean minionless, target;

    @Inject(method = "isPlayerInChunk", at = @At("RETURN"), cancellable = true)
    public void filterMinions(long chunkPos, CallbackInfoReturnable<Boolean> cir) {
        if (minionless) {
            cir.setReturnValue(isRealPlayerInChunk(chunkPos));
        }
    }

    @Unique
    public boolean isRealPlayerInChunk(long chunkPos) {
        ObjectSet<ServerPlayerEntity> players = ((ChunkTicketManagerAccessor)field_17462).getPlayers(chunkPos);
        boolean contains = false;
        if(players != null) {
            contains = players.stream().anyMatch(player -> {
                if (player instanceof MinionFakePlayer minion) {
                    return MobSpawningModule.canMinionSpawnMobs(minion);
                }
                return true;
            });
        }
        return contains;
    }

    @Inject(method = "updateLevels", at = @At("HEAD"))
    public void sync(CallbackInfo info) {
        if (target) {
            ((ChunkTicketManagerAccessor)field_17462).getMinionless().updateLevels();
        }
    }

    @Override
    public void minions$updateLevel(long chunkPos, int distance, boolean decrease, CallbackInfo info) {
        if (target && (distance != 0 || isRealPlayerInChunk(chunkPos))) {
            ((ChunkTicketManagerAccessor)field_17462).getMinionless().updateLevel(chunkPos, distance, decrease);
        }
    }

    @Override
    public void minions$markAsMinionless() {
        minionless = true;
    }

    @Override
    public void minions$markAsTarget() {
        target = true;
    }

    @Override
    public int minions$getTickedChunkCount() {
        return distanceFromNearestPlayer.size();
    }
}

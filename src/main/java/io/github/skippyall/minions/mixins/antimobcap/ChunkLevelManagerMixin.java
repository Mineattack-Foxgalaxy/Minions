package io.github.skippyall.minions.mixins.antimobcap;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.skippyall.minions.mixinhelper.ChunkLevelManager$DistanceFromNearestPlayerTrackerAccessor;
import io.github.skippyall.minions.mixinhelper.ChunkLevelManagerAccessor;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkLevelManager;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.util.math.ChunkSectionPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.Executor;

@Mixin(ChunkLevelManager.class)
public class ChunkLevelManagerMixin implements ChunkLevelManagerAccessor {
    @Shadow @Final Long2ObjectMap<ObjectSet<ServerPlayerEntity>> playersByChunkPos;
    @Shadow @Final private ChunkLevelManager.DistanceFromNearestPlayerTracker distanceFromNearestPlayerTracker;
    @Unique
    ChunkLevelManager.DistanceFromNearestPlayerTracker minionless;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void createMinionlessClone(ChunkTicketManager ticketManager, Executor executor, Executor mainThreadExecutor, CallbackInfo ci) {
        ChunkLevelManager manager = ((ChunkLevelManager)(Object)this);
        minionless = manager.new DistanceFromNearestPlayerTracker(8);
        ((ChunkLevelManager$DistanceFromNearestPlayerTrackerAccessor)minionless).minions$markAsMinionless();
        ((ChunkLevelManager$DistanceFromNearestPlayerTrackerAccessor)distanceFromNearestPlayerTracker).minions$markAsTarget();
    }

    public ObjectSet<ServerPlayerEntity> minions$getPlayers(long chunkpos) {
        return playersByChunkPos.get(chunkpos);
    }

    @Override
    public ChunkLevelManager.DistanceFromNearestPlayerTracker minions$getMinionless() {
        return minionless;
    }

    @Inject(method = "handleChunkLeave", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectSet;remove(Ljava/lang/Object;)Z", shift = At.Shift.AFTER, remap = false))
    public void minion$updateMinionlessIfNoMinionInChunk(ChunkSectionPos pos, ServerPlayerEntity player, CallbackInfo ci, @Local long chunk) {
        if (!((ChunkLevelManager$DistanceFromNearestPlayerTrackerAccessor)minionless).minions$isRealPlayerInChunk(chunk)) {
            minionless.updateLevel(chunk, Integer.MAX_VALUE, false);
        }
    }
}

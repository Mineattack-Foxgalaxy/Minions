package io.github.skippyall.minions.mixins.mobcap;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.skippyall.minions.mixinhelper.antimobcap.ChunkLevelManager$DistanceFromNearestPlayerTrackerAccessor;
import io.github.skippyall.minions.mixinhelper.antimobcap.ChunkLevelManagerAccessor;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.TicketStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.Executor;

@Mixin(DistanceManager.class)
public class DistanceManagerMixin implements ChunkLevelManagerAccessor {
    @Shadow @Final
    private Long2ObjectMap<ObjectSet<ServerPlayer>> playersPerChunk;
    @Shadow @Final
    private DistanceManager.FixedPlayerDistanceChunkTracker naturalSpawnChunkCounter;
    @Unique
    DistanceManager.FixedPlayerDistanceChunkTracker minionless;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void createMinionlessClone(TicketStorage ticketManager, Executor executor, Executor mainThreadExecutor, CallbackInfo ci) {
        DistanceManager manager = ((DistanceManager)(Object)this);
        minionless = manager.new FixedPlayerDistanceChunkTracker(8);
        ((ChunkLevelManager$DistanceFromNearestPlayerTrackerAccessor)minionless).minions$markAsMinionless();
        ((ChunkLevelManager$DistanceFromNearestPlayerTrackerAccessor)naturalSpawnChunkCounter).minions$markAsTarget();
    }

    public ObjectSet<ServerPlayer> minions$getPlayers(long chunkpos) {
        return playersPerChunk.get(chunkpos);
    }

    @Override
    public DistanceManager.FixedPlayerDistanceChunkTracker minions$getMinionless() {
        return minionless;
    }

    @Inject(method = "removePlayer", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectSet;remove(Ljava/lang/Object;)Z", shift = At.Shift.AFTER, remap = false))
    public void minion$updateMinionlessIfNoMinionInChunk(SectionPos pos, ServerPlayer player, CallbackInfo ci, @Local long chunk) {
        if (!((ChunkLevelManager$DistanceFromNearestPlayerTrackerAccessor)minionless).minions$isRealPlayerInChunk(chunk)) {
            minionless.update(chunk, Integer.MAX_VALUE, false);
        }
    }
}

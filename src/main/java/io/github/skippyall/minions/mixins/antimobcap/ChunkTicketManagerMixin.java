package io.github.skippyall.minions.mixins.antimobcap;

import io.github.skippyall.minions.mixinhelper.ChunkTicketManager$DistanceFromNearestPlayerTrackerAccessor;
import io.github.skippyall.minions.mixinhelper.ChunkTicketManagerAccessor;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.Executor;

@Mixin(ChunkTicketManager.class)
public class ChunkTicketManagerMixin implements ChunkTicketManagerAccessor {
    @Shadow @Final private Long2ObjectMap<ObjectSet<ServerPlayerEntity>> playersByChunkPos;
    @Shadow @Final private ChunkTicketManager.DistanceFromNearestPlayerTracker distanceFromNearestPlayerTracker;
    ChunkTicketManager.DistanceFromNearestPlayerTracker minionless;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void createMinionlessClone(Executor workerExecutor, Executor mainThreadExecutor, CallbackInfo ci) {
        ChunkTicketManager manager = ((ChunkTicketManager)(Object)this);
        minionless = manager.new DistanceFromNearestPlayerTracker(8);
        ((ChunkTicketManager$DistanceFromNearestPlayerTrackerAccessor)minionless).minions$markAsMinionless();
        ((ChunkTicketManager$DistanceFromNearestPlayerTrackerAccessor)distanceFromNearestPlayerTracker).minions$markAsTarget();
    }

    public ObjectSet<ServerPlayerEntity> getPlayers(long chunkpos) {
        return playersByChunkPos.get(chunkpos);
    }

    @Override
    public ChunkTicketManager.DistanceFromNearestPlayerTracker getMinionless() {
        return minionless;
    }
}

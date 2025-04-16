package io.github.skippyall.minions.mixins.antimobcap;

import io.github.skippyall.minions.mixinhelper.ChunkTicketManager$DistanceFromNearestPlayerTrackerAccessor;
import io.github.skippyall.minions.mixinhelper.ChunkTicketManagerAccessor;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ServerChunkManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerChunkManager.class)
public class ServerChunkManagerMixin {
    @Shadow
    @Final
    private ChunkTicketManager ticketManager;

    @ModifyArg(method = "tickChunks(Lnet/minecraft/util/profiler/Profiler;JLjava/util/List;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/SpawnHelper;setupSpawn(ILjava/lang/Iterable;Lnet/minecraft/world/SpawnHelper$ChunkSource;Lnet/minecraft/world/SpawnDensityCapper;)Lnet/minecraft/world/SpawnHelper$Info;"))
    public int useMinionless(int spawningChunkCount) {
        return ((ChunkTicketManager$DistanceFromNearestPlayerTrackerAccessor)((ChunkTicketManagerAccessor)ticketManager).getMinionless()).minions$getTickedChunkCount();
    }
}

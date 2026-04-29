package io.github.skippyall.minions.mixins.antimobcap;

import net.minecraft.server.level.ChunkTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkTracker.class)
public class ChunkTrackerMixin {
    @Inject(method = "update", at = @At("HEAD"))
    public void minions$updateLevel(long chunkPos, int distance, boolean decrease, CallbackInfo info) {

    }
}

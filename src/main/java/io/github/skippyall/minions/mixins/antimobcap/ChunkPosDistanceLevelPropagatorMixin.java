package io.github.skippyall.minions.mixins.antimobcap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.world.ChunkPosDistanceLevelPropagator.class)
public class ChunkPosDistanceLevelPropagatorMixin {
    @Inject(method = "updateLevel", at = @At("HEAD"))
    public void minions$updateLevel(long chunkPos, int distance, boolean decrease, CallbackInfo info) {

    }
}

package io.github.skippyall.minions.mixins;

import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract @Nullable LivingEntity getControllingPassenger();

    @Shadow
    private World world;

    @Inject(method = "isLogicalSideForUpdatingMovement", at = @At("HEAD"), cancellable = true)
    private void isFakePlayer(CallbackInfoReturnable<Boolean> cir)
    {
        if (getControllingPassenger() instanceof MinionFakePlayer) cir.setReturnValue(!world.isClient);
    }
}

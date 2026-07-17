//code from https://github.com/gnembon/fabric-carpet/blob/master/src/main/java/carpet/mixins/EntityMixin.java
package io.github.skippyall.minions.mixins;

import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
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
    private Level level;

    @Inject(method = "isLocalInstanceAuthoritative", at = @At("HEAD"), cancellable = true)
    private void isFakePlayer(CallbackInfoReturnable<Boolean> cir)
    {
        if (getControllingPassenger() instanceof MinionFakePlayer) cir.setReturnValue(!level.isClientSide());
    }
}

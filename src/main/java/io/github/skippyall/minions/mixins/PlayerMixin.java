//code from https://github.com/gnembon/fabric-carpet
package io.github.skippyall.minions.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin {
    /**
     * To make sure player attacks are able to knockback fake players
     */
    @ModifyExpressionValue(
            method = "attack",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/Entity;velocityModified:Z",
                    ordinal = 0
            )
    )
    private boolean velocityModifiedAndNotCarpetFakePlayer(boolean value, @Local(argsOnly = true) Entity entity) {
        return value && !(entity instanceof MinionFakePlayer);
    }
}

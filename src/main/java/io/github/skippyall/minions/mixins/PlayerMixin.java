//code from https://github.com/gnembon/fabric-carpet
package io.github.skippyall.minions.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin {
    /**
     * To make sure player attacks are able to knockback fake players
     */
    @ModifyExpressionValue(
            method = "attack",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/Entity;hurtMarked:Z",
                    ordinal = 0,
                    opcode = Opcodes.GETFIELD
            )
    )
    private boolean velocityModifiedAndNotCarpetFakePlayer(boolean value, @Local(argsOnly = true) Entity entity) {
        return value && !(entity instanceof MinionFakePlayer);
    }
}

package io.github.skippyall.minions.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TickRateManager.class)
public abstract class TickRateManagerMixin {
    @Shadow
    public abstract boolean runsNormally();

    @ModifyReturnValue(method = "isEntityFrozen", at = @At("TAIL"))
    private boolean handler(boolean alreadyFrozen, Entity entity) {
        if (alreadyFrozen) return true;
        if (runsNormally()) return false;

        return !isActualPlayer(entity) && // not carrying players
                ((EntityAccessor) entity)
                        .minions$streamIntoPassengers()
                        .noneMatch(TickRateManagerMixin::isActualPlayer);
    }

    @Unique
    private static boolean isActualPlayer(Entity e) {
        return e instanceof Player && !(e instanceof MinionFakePlayer);
    }
}

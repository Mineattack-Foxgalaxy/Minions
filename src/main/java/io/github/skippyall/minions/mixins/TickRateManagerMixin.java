package io.github.skippyall.minions.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.tick.TickManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TickManager.class)
public abstract class TickRateManagerMixin {
    @Shadow
    public abstract boolean shouldTick();

    @ModifyReturnValue(method = "shouldSkipTick", at = @At("TAIL"))
    private boolean handler(boolean alreadyFrozen, Entity entity) {
        if (alreadyFrozen) return true;
        if (shouldTick()) return false;

        return !isActualPlayer(entity) && // not carrying players
                ((EntityAccessor) entity)
                        .minions$streamIntoPassengers()
                        .noneMatch(TickRateManagerMixin::isActualPlayer);
    }

    @Unique
    private static boolean isActualPlayer(Entity e) {
        return e instanceof PlayerEntity && !(e instanceof MinionFakePlayer);
    }
}

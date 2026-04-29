package io.github.skippyall.minions.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.mixinhelper.EntityViewMixinHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public abstract class MobMixin {
    @WrapOperation(method = "checkDespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getNearestPlayer(Lnet/minecraft/world/entity/Entity;D)Lnet/minecraft/world/entity/player/Player;"))
    public Player checkMobDespawningMinion(Level instance, Entity entity, double maxDistance, Operation<Player> original) {
        EntityViewMixinHelper.ADDITIONAL_PREDICATE.set(entity2 -> {
            if(entity2 instanceof MinionFakePlayer minion) {
                return minion.canDespawnMobs();
            } else {
                return true;
            }
        });
        Player player = original.call(instance, entity, maxDistance);
        EntityViewMixinHelper.ADDITIONAL_PREDICATE.remove();
        return player;
    }
}

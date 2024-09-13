package io.github.skippyall.minions.mixins;

import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.module.MobSpawningModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {
    @Redirect(method = "checkDespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getClosestPlayer(Lnet/minecraft/entity/Entity;D)Lnet/minecraft/entity/player/PlayerEntity;"))
    public PlayerEntity checkMobDespawningMinion(World instance, Entity entity, double maxDistance) {
        return instance.getClosestPlayer(entity.getX(), entity.getY(), entity.getZ(), maxDistance, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.and(entity1 -> {
            if(entity1 instanceof ServerPlayerEntity player) {
                if(player instanceof MinionFakePlayer minion) {
                    return MobSpawningModule.canMinionDespawnMobs(minion);
                }
                return true;
            }
            return false;
        }));
    }
}

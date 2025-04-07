package io.github.skippyall.minions.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.mixinhelpers.EntityViewMixinHelper;
import io.github.skippyall.minions.module.MobSpawningModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {
    @WrapOperation(method = "checkDespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getClosestPlayer(Lnet/minecraft/entity/Entity;D)Lnet/minecraft/entity/player/PlayerEntity;"))
    public PlayerEntity checkMobDespawningMinion(World instance, Entity entity, double maxDistance, Operation<PlayerEntity> original) {
        EntityViewMixinHelper.ADDITIONAL_PREDICATE.set(entity2 -> {
            if(entity2 instanceof MinionFakePlayer minion) {
                return MobSpawningModule.canMinionDespawnMobs(minion);
            } else {
                return true;
            }
        });
        PlayerEntity player = original.call(instance, entity, maxDistance);
        EntityViewMixinHelper.ADDITIONAL_PREDICATE.remove();
        return player;
    }
}

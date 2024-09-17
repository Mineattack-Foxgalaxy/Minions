package io.github.skippyall.minions.mixins;

import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.module.MobSpawningModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.SpawnHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpawnHelper.class)
public class SpawnHelperMixin {
    @Redirect(method = "spawnEntitiesInChunk(Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/SpawnHelper$Checker;Lnet/minecraft/world/SpawnHelper$Runner;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getClosestPlayer(DDDDZ)Lnet/minecraft/entity/player/PlayerEntity;"))
    private static PlayerEntity checkMobSpawningMinion(ServerWorld instance, double x, double y, double z, double maxDistance, boolean b) {
        return instance.getClosestPlayer(x, y, z, maxDistance, EntityPredicates.EXCEPT_SPECTATOR.and(entity -> {
            if(entity instanceof ServerPlayerEntity player) {
                if(player instanceof MinionFakePlayer minion) {
                    return MobSpawningModule.canMinionSpawnMobs(minion);
                }
                return true;
            }
            return false;
        }));
    }
}

package io.github.skippyall.minions.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.mixinhelper.EntityViewMixinHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.SpawnHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpawnHelper.class)
public class SpawnHelperMixin {
    @WrapOperation(method = "spawnEntitiesInChunk(Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/SpawnHelper$Checker;Lnet/minecraft/world/SpawnHelper$Runner;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getClosestPlayer(DDDDZ)Lnet/minecraft/entity/player/PlayerEntity;"))
    private static PlayerEntity checkMobSpawningMinion(ServerWorld world, double x, double y, double z, double maxDistance, boolean ignoreCreative, Operation<PlayerEntity> original) {
        EntityViewMixinHelper.ADDITIONAL_PREDICATE.set(entity -> {
            if(entity instanceof ServerPlayerEntity player) {
                if(player instanceof MinionFakePlayer minion) {
                    return minion.canSpawnMobs();
                }
                return true;
            }
            return false;
        });
        PlayerEntity player = original.call(world, x, y, z, maxDistance, ignoreCreative);
        EntityViewMixinHelper.ADDITIONAL_PREDICATE.remove();
        return player;
    }
}

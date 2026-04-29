package io.github.skippyall.minions.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.mixinhelper.EntityViewMixinHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NaturalSpawner.class)
public class NaturalSpawnerMixin {
    @WrapOperation(method = "spawnCategoryForPosition(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/NaturalSpawner$SpawnPredicate;Lnet/minecraft/world/level/NaturalSpawner$AfterSpawnCallback;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getNearestPlayer(DDDDZ)Lnet/minecraft/world/entity/player/Player;"))
    private static Player checkMobSpawningMinion(ServerLevel world, double x, double y, double z, double maxDistance, boolean ignoreCreative, Operation<Player> original) {
        EntityViewMixinHelper.ADDITIONAL_PREDICATE.set(entity -> {
            if(entity instanceof ServerPlayer player) {
                if(player instanceof MinionFakePlayer minion) {
                    return minion.canSpawnMobs();
                }
                return true;
            }
            return false;
        });
        Player player = original.call(world, x, y, z, maxDistance, ignoreCreative);
        EntityViewMixinHelper.ADDITIONAL_PREDICATE.remove();
        return player;
    }
}

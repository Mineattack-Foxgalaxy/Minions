package io.github.skippyall.minions.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PistonMovingBlockEntity.class)
public abstract class PistonMovingBlockEntityMixin {
    @WrapOperation(method = "moveCollidedEntities", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;getPistonPushReaction()Lnet/minecraft/world/level/material/PushReaction;"
    ))
    private static PushReaction moveFakePlayers(Entity entity, Operation<PushReaction> original, Level world, BlockPos pos, float f, PistonMovingBlockEntity pistonBlockEntity)
    {
        if (entity instanceof MinionFakePlayer && pistonBlockEntity.getMovedState().is(Blocks.SLIME_BLOCK))
        {
            Vec3 vec3d = entity.getDeltaMovement();
            double x = vec3d.x;
            double y = vec3d.y;
            double z = vec3d.z;
            Direction direction = pistonBlockEntity.getMovementDirection();
            switch (direction.getAxis()) {
                case X -> x = direction.getStepX();
                case Y -> y = direction.getStepY();
                case Z -> z = direction.getStepZ();
            }

            entity.setDeltaMovement(x, y, z);
        }
        return original.call(entity);
    }
}

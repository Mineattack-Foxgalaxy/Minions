package io.github.skippyall.minions.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PistonBlockEntity.class)
public abstract class PistonMovingBlockEntityMixin {
    @WrapOperation(method = "pushEntities", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;getPistonBehavior()Lnet/minecraft/block/piston/PistonBehavior;"
    ))
    private static PistonBehavior moveFakePlayers(Entity entity, Operation<PistonBehavior> original, World world, BlockPos pos, float f, PistonBlockEntity pistonBlockEntity)
    {
        if (entity instanceof MinionFakePlayer && pistonBlockEntity.getPushedBlock().isOf(Blocks.SLIME_BLOCK))
        {
            Vec3d vec3d = entity.getVelocity();
            double x = vec3d.x;
            double y = vec3d.y;
            double z = vec3d.z;
            Direction direction = pistonBlockEntity.getMovementDirection();
            switch (direction.getAxis()) {
                case X -> x = direction.getOffsetX();
                case Y -> y = direction.getOffsetY();
                case Z -> z = direction.getOffsetZ();
            }

            entity.setVelocity(x, y, z);
        }
        return original.call(entity);
    }
}

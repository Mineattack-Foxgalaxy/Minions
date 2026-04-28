package io.github.skippyall.minions.block.input;

import io.github.skippyall.minions.clipboard.ClipboardItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AnalogInputBlock extends Block {
    public AnalogInputBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if(!world.isClient) {
            player.getInventory().offer(ClipboardItem.createBlockPosReference(world, pos), true);
        }
        return ActionResult.SUCCESS;
    }
}

package io.github.skippyall.minions.block.input;

import io.github.skippyall.minions.clipboard.ClipboardItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class AnalogInputBlock extends Block {
    public AnalogInputBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if(!world.isClientSide) {
            player.getInventory().placeItemBackInInventory(ClipboardItem.createBlockPosReference(world, pos), true);
        }
        return InteractionResult.SUCCESS;
    }
}

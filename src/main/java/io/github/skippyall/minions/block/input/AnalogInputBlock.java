package io.github.skippyall.minions.block.input;

import io.github.skippyall.minions.clipboard.ClipboardItem;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.registration.ValueTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class AnalogInputBlock extends Block implements ValueProvider.Block {
    public AnalogInputBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if(!world.isClientSide()) {
            player.getInventory().placeItemBackInInventory(ClipboardItem.createBlockPosReference(world, pos), true);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public TypedValue<?> getValue(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction direction) {
        return new TypedValue<>((long) level.getBestNeighborSignal(pos), ValueTypes.LONG);
    }
}

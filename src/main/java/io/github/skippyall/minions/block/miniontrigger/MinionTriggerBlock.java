package io.github.skippyall.minions.block.miniontrigger;

import com.mojang.serialization.MapCodec;
import io.github.skippyall.minions.clipboard.MinionClipboard;
import io.github.skippyall.minions.registration.MinionBlocks;
import io.github.skippyall.minions.registration.MinionComponentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

public class MinionTriggerBlock extends Block implements EntityBlock {
    public static final MapCodec<MinionTriggerBlock> CODEC = simpleCodec(MinionTriggerBlock::new);

    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final VoxelShape SHAPE = Block.column(16.0, 0.0, 8.0);

    public MinionTriggerBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(itemStack.get(MinionComponentTypes.REFERENCE) instanceof MinionClipboard minion) {
            level.getBlockEntity(pos, MinionBlocks.MINION_TRIGGER_BE_TYPE).ifPresent(be -> be.setMinion(minion.minion()));
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if(player instanceof ServerPlayer serverPlayer && level.getBlockEntity(pos) instanceof MinionTriggerBlockEntity be) {
            if(be.openGui(serverPlayer)) {
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos blockPos = pos.below();
        return this.canPlaceAbove(world, blockPos, world.getBlockState(blockPos));
    }

    protected boolean canPlaceAbove(LevelReader world, BlockPos pos, BlockState state) {
        return state.isFaceSturdy(world, pos, Direction.UP, SupportType.RIGID);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    @NullMarked
    protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, @Nullable Orientation wireOrientation, boolean notify) {
        if(!canSurvive(state, world, pos)) {
            dropResources(state, world, pos);
            world.removeBlock(pos, false);
            return;
        }

        boolean newPower = world.hasNeighborSignal(pos);
        if(state.getValue(POWERED) != newPower) {
            world.setBlockAndUpdate(pos, state.setValue(POWERED, newPower));
            world.getBlockEntity(pos, MinionBlocks.MINION_TRIGGER_BE_TYPE).ifPresent(MinionTriggerBlockEntity::updatePower);
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
        return world.getBlockEntity(pos, MinionBlocks.MINION_TRIGGER_BE_TYPE).map(MinionTriggerBlockEntity::getComparatorOutput).orElse(0);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MinionTriggerBlockEntity(pos, state);
    }
}

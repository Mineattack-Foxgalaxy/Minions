package io.github.skippyall.minions.block;

import com.mojang.serialization.MapCodec;
import io.github.skippyall.minions.block.input.ValueProvider;
import io.github.skippyall.minions.registration.MinionBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class ConnectorBlock extends PipeBlock {

    public ConnectorBlock(Properties properties) {
        super(10F, properties);
        this.registerDefaultState(
                this.stateDefinition
                        .any()
                        .setValue(NORTH, false)
                        .setValue(EAST, false)
                        .setValue(SOUTH, false)
                        .setValue(WEST, false)
                        .setValue(UP, false)
                        .setValue(DOWN, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        for (BooleanProperty value : PipeBlock.PROPERTY_BY_DIRECTION.values()) {
            builder.add(value);
        }
    }

    @Override
    protected MapCodec<? extends PipeBlock> codec() {
        return simpleCodec(ConnectorBlock::new);
    }

    public boolean canConnectTo(LevelReader levelReader, BlockPos neighbourPos, BlockState neighbourState, Direction directionToNeighbour) {
        if(neighbourState.getBlock() == MinionBlocks.TRIGGER_CONNECTOR || neighbourState.getBlock() == MinionBlocks.MINION_TRIGGER) {
            return true;
        } else if (levelReader instanceof Level level) {
            return ValueProvider.SIDED.find(level, neighbourPos, neighbourState, level.getBlockEntity(neighbourPos), directionToNeighbour.getOpposite()) != null;
        } else {
            return false;
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        boolean shouldConnect = canConnectTo(level, neighbourPos, neighbourState, directionToNeighbour);
        return state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(directionToNeighbour), shouldConnect);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        BlockState newState = state;
        for(Direction dir : Direction.values()) {
            BlockPos neighbourPos = pos.relative(dir);
            if(canConnectTo(level, neighbourPos, level.getBlockState(neighbourPos), dir)) {
                newState = newState.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(dir), true);
            }
        }

        if(state != newState) {
            level.setBlock(pos, newState, ConnectorBlock.UPDATE_CLIENTS);
        }
    }
}

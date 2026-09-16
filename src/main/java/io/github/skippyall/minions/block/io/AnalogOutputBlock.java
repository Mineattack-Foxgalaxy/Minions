package io.github.skippyall.minions.block.io;

import io.github.skippyall.minions.program.conversion.Casts;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionBlocks;
import io.github.skippyall.minions.registration.ValueTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jspecify.annotations.Nullable;

public class AnalogOutputBlock extends Block {
    public static final IntegerProperty SIGNAL = IntegerProperty.create("signal", 0, 15);

    public AnalogOutputBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(SIGNAL, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SIGNAL);
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(SIGNAL);
    }

    public void outputSignal(int signal, Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(SIGNAL, signal), Block.UPDATE_ALL);
        level.updateNeighborsAt(pos, MinionBlocks.ANALOG_OUTPUT);
    }

    public BlockValueConsumer getBlockValueConsumer(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction context) {
        return new BlockValueConsumer() {
            @Override
            public void acceptValue(TypedValue<?> value) {
                Casts.cast(value, ValueTypes.LONG).ifSuccess(signal -> {
                    outputSignal(signal.intValue(), level, pos, state);
                });
            }

            @Override
            public ValueType<?> getType() {
                return ValueTypes.LONG;
            }
        };
    }
}

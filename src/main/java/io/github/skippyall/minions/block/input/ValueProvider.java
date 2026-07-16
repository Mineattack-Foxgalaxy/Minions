package io.github.skippyall.minions.block.input;

import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.program.value.TypedValue;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public interface ValueProvider {
    BlockApiLookup<ValueProvider, @Nullable Direction> SIDED = BlockApiLookup.get(Minions.id("value_provider"), ValueProvider.class, Direction.class);

    TypedValue<?> getValue();

    interface Block extends BlockApiLookup.BlockApiProvider<ValueProvider, @Nullable Direction> {
        @Override
        default @Nullable ValueProvider find(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction context) {
            return () -> getValue(level, pos, state, blockEntity, context);
        }

        TypedValue<?> getValue(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction direction);
    }
}

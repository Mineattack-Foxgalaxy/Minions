package io.github.skippyall.minions.polymer.block;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

public class ConnectorBlockOverlay implements PolymerBlock {
    @Override
    public BlockState getPolymerBlockState(BlockState state, @Nullable PacketContext context) {
        BlockState polymerState = Blocks.CHORUS_PLANT.defaultBlockState();
        for(Property<Boolean> property : PipeBlock.PROPERTY_BY_DIRECTION.values()) {
            polymerState = polymerState.setValue(property, state.getValue(property));
        }
        return polymerState;
    }
}

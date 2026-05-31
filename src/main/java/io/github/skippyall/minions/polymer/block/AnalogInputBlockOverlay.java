package io.github.skippyall.minions.polymer.block;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class AnalogInputBlockOverlay implements PolymerBlock {
    @Override
    public BlockState getPolymerBlockState(BlockState blockState, @Nullable PacketContext packetContext) {
        return Blocks.AMETHYST_BLOCK.defaultBlockState();
    }
}

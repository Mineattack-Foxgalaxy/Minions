package io.github.skippyall.minions.clipboard;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Consumer;

public record BlockPosClipboard(RegistryKey<World> world, BlockPos pos) implements Clipboard {
    public static final MapCodec<BlockPosClipboard> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    World.CODEC.fieldOf("world").forGetter(BlockPosClipboard::world),
                    BlockPos.CODEC.fieldOf("pos").forGetter(BlockPosClipboard::pos)
            ).apply(instance, BlockPosClipboard::new)
    );


    @Override
    public MapCodec<BlockPosClipboard> getCodec() {
        return CODEC;
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
        textConsumer.accept(Text.translatable("minions.reference.block.tooltip", pos.toString()));
    }
}

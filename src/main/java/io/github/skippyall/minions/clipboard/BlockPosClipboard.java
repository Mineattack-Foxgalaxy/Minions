package io.github.skippyall.minions.clipboard;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public record BlockPosClipboard(ResourceKey<Level> world, BlockPos pos) implements Clipboard {
    public static final MapCodec<BlockPosClipboard> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Level.RESOURCE_KEY_CODEC.fieldOf("world").forGetter(BlockPosClipboard::world),
                    BlockPos.CODEC.fieldOf("pos").forGetter(BlockPosClipboard::pos)
            ).apply(instance, BlockPosClipboard::new)
    );


    @Override
    public MapCodec<BlockPosClipboard> getCodec() {
        return CODEC;
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> textConsumer, TooltipFlag type, DataComponentGetter components) {
        textConsumer.accept(Component.translatable("minions.reference.block.tooltip", pos.toString()));
    }
}

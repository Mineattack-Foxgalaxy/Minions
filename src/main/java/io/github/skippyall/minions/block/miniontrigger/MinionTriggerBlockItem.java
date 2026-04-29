package io.github.skippyall.minions.block.miniontrigger;

import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public class MinionTriggerBlockItem extends PolymerBlockItem {
    public MinionTriggerBlockItem(Block block, Properties settings, Item polymerItem) {
        super(block, settings, polymerItem, true);
    }

    @Override
    public @Nullable ResourceLocation getPolymerItemModel(ItemStack stack, PacketContext context) {
        if(PolymerResourcePackUtils.hasMainPack(context)) {
            return super.getPolymerItemModel(stack, context);
        } else {
            return null;
        }
    }
}

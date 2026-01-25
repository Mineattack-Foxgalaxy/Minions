package io.github.skippyall.minions.block.miniontrigger;

import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public class MinionTriggerBlockItem extends PolymerBlockItem {
    public MinionTriggerBlockItem(Block block, Settings settings, Item polymerItem) {
        super(block, settings, polymerItem, true);
    }

    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context) {
        if(PolymerResourcePackUtils.hasMainPack(context)) {
            return super.getPolymerItemModel(stack, context);
        } else {
            return null;
        }
    }
}

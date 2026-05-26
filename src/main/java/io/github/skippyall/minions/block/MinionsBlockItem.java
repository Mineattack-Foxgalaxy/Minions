package io.github.skippyall.minions.block;

import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import io.github.skippyall.minions.polymer.VersionSync;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

public class MinionsBlockItem extends PolymerBlockItem {
    public MinionsBlockItem(Block block, Properties settings, Item polymerItem) {
        super(block, settings, polymerItem, true);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        if(VersionSync.isOnClient(context)) {
            return this;
        } else {
            return super.getPolymerItem(itemStack, context);
        }
    }

    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context, HolderLookup.Provider lookup) {
        if(PolymerResourcePackUtils.hasMainPack(context)) {
            return super.getPolymerItemModel(stack, context, lookup);
        } else {
            return null;
        }
    }
}

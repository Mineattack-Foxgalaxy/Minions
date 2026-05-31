package io.github.skippyall.minions.polymer.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import org.jspecify.annotations.Nullable;

public class MinionItemOverlay implements PolymerItem {
    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context, HolderLookup.Provider lookup) {
        return null;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext player) {
        return Items.ARMOR_STAND;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack stack, TooltipFlag tooltipType, PacketContext player, HolderLookup.Provider lookup) {
        ItemStack out = PolymerItem.super.getPolymerItemStack(stack, tooltipType, player, lookup);
        out.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        return out;
    }
}

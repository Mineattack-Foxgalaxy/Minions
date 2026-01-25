package io.github.skippyall.minions.reference;

import eu.pb4.polymer.core.api.item.PolymerItem;
import io.github.skippyall.minions.registration.MinionItems;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public class ReferenceItem extends Item implements PolymerItem {
    public ReferenceItem(Settings settings) {
        super(settings);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.PAPER;
    }

    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context) {
        return null;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipType tooltipType, PacketContext context) {
        ItemStack stack = PolymerItem.super.getPolymerItemStack(itemStack, tooltipType, context);
        stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        return stack;
    }

    public static ItemStack createInstructionReference(MinionFakePlayer minion, String instructionName) {
        ItemStack stack = new ItemStack(MinionItems.REFERENCE_ITEM);
        stack.set(Reference.COMPONENT_TYPE, new InstructionReference(minion.getUuid(), instructionName, minion.getGameProfile().getName()));
        return stack;
    }
}

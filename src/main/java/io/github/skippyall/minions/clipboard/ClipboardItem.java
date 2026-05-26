package io.github.skippyall.minions.clipboard;

import eu.pb4.polymer.core.api.item.PolymerItem;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.registration.MinionComponentTypes;
import io.github.skippyall.minions.registration.MinionItems;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class ClipboardItem extends Item implements PolymerItem {
    public ClipboardItem(Properties settings) {
        super(settings);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return /*VersionSync.isOnClient(context) ? this : */Items.PAPER;
    }

    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context, HolderLookup.Provider lookup) {
        return null;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipFlag tooltipType, PacketContext context, HolderLookup.Provider lookup) {
        ItemStack stack = PolymerItem.super.getPolymerItemStack(itemStack, tooltipType, context, lookup);
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        return stack;
    }

    public static ItemStack createInstructionReference(MinionFakePlayer minion, String instructionName) {
        ItemStack stack = new ItemStack(MinionItems.REFERENCE_ITEM);
        stack.set(MinionComponentTypes.REFERENCE, new InstructionClipboard(minion.getUUID(), instructionName, minion.getGameProfile().name()));
        return stack;
    }

    public static ItemStack createBlockPosReference(Level world, BlockPos pos) {
        ItemStack stack = new ItemStack(MinionItems.REFERENCE_ITEM);
        stack.set(MinionComponentTypes.REFERENCE, new BlockPosClipboard(world.dimension(), pos));
        return stack;
    }
}

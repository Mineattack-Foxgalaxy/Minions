package io.github.skippyall.minions.clipboard;

import eu.pb4.polymer.core.api.item.PolymerItem;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.registration.MinionComponentTypes;
import io.github.skippyall.minions.registration.MinionItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public class ClipboardItem extends Item implements PolymerItem {
    public ClipboardItem(Properties settings) {
        super(settings);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return /*VersionSync.isOnClient(context) ? this : */Items.PAPER;
    }

    @Override
    public @Nullable ResourceLocation getPolymerItemModel(ItemStack stack, PacketContext context) {
        return null;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipFlag tooltipType, PacketContext context) {
        ItemStack stack = PolymerItem.super.getPolymerItemStack(itemStack, tooltipType, context);
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        return stack;
    }

    public static ItemStack createInstructionReference(MinionFakePlayer minion, String instructionName) {
        ItemStack stack = new ItemStack(MinionItems.REFERENCE_ITEM);
        stack.set(MinionComponentTypes.REFERENCE, new InstructionClipboard(minion.getUUID(), instructionName, minion.getGameProfile().getName()));
        return stack;
    }

    public static ItemStack createBlockPosReference(Level world, BlockPos pos) {
        ItemStack stack = new ItemStack(MinionItems.REFERENCE_ITEM);
        stack.set(MinionComponentTypes.REFERENCE, new BlockPosClipboard(world.dimension(), pos));
        return stack;
    }
}

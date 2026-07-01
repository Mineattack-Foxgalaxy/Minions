package io.github.skippyall.minions.clipboard;

import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.registration.MinionComponentTypes;
import io.github.skippyall.minions.registration.MinionItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ClipboardItem {

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

    public static ItemStack createMinionReference(MinionFakePlayer minion) {
        ItemStack stack = new ItemStack(MinionItems.REFERENCE_ITEM);
        stack.set(MinionComponentTypes.REFERENCE, new MinionClipboard(minion.getUUID(), minion.getGameProfile().name()));
        return stack;
    }
}

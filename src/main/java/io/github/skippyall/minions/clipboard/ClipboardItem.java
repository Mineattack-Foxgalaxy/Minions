package io.github.skippyall.minions.clipboard;

import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.registration.MinionComponentTypes;
import io.github.skippyall.minions.registration.MinionItems;
import net.minecraft.world.item.ItemStack;

public class ClipboardItem {
    public static ItemStack createMinionReference(MinionFakePlayer minion) {
        ItemStack stack = new ItemStack(MinionItems.REFERENCE_ITEM);
        stack.set(MinionComponentTypes.REFERENCE, new MinionClipboard(minion.getUUID(), minion.getGameProfile().name()));
        return stack;
    }
}

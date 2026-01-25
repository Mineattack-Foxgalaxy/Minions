package io.github.skippyall.minions.registration;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import io.github.skippyall.minions.Minions;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class MinionCreativeTab {
    public static ItemGroup group;

    private static final List<ItemStack> items = new ArrayList<>();

    public static void add(Item entry) {
        items.add(entry.getDefaultStack());
    }

    public static void collectEntries(ItemGroup.DisplayContext displayContext, ItemGroup.Entries entries) {
        entries.addAll(items);
    }

    public static void registerGroup() {
        group = FabricItemGroup.builder()
                .displayName(Text.translatable("minions.generic.mod_name"))
                .icon(MinionItems.MINION_ITEM::getDefaultStack)
                .entries(MinionCreativeTab::collectEntries)
                .build();
        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(Minions.MOD_ID, "main"), group);
    }
}

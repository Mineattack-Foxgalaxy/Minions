package io.github.skippyall.minions.registration;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import io.github.skippyall.minions.Minions;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class MinionCreativeTab {
    public static CreativeModeTab group;

    private static final List<ItemStack> items = new ArrayList<>();

    public static void add(Item entry) {
        items.add(entry.getDefaultInstance());
    }

    public static void collectEntries(CreativeModeTab.ItemDisplayParameters displayContext, CreativeModeTab.Output entries) {
        entries.acceptAll(items);
    }

    public static void registerGroup() {
        group = FabricItemGroup.builder()
                .title(Component.translatable("minions.generic.mod_name"))
                .icon(MinionItems.MINION_ITEM::getDefaultInstance)
                .displayItems(MinionCreativeTab::collectEntries)
                .build();
        PolymerItemGroupUtils.registerPolymerItemGroup(ResourceLocation.fromNamespaceAndPath(Minions.MOD_ID, "main"), group);
    }
}

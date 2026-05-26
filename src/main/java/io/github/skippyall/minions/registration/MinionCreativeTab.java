package io.github.skippyall.minions.registration;

import eu.pb4.polymer.core.api.item.PolymerCreativeModeTabUtils;
import io.github.skippyall.minions.Minions;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MinionCreativeTab {
    @Nullable
    public static CreativeModeTab group;

    private static final List<Item> items = new ArrayList<>();

    public static void add(Item entry) {
        items.add(entry);
    }

    public static void collectEntries(CreativeModeTab.ItemDisplayParameters displayContext, CreativeModeTab.Output entries) {
        for(Item item : items) {
            entries.accept(item);
        }
    }

    public static void registerGroup() {
        group = FabricCreativeModeTab.builder()
                .title(Component.translatable("minions.generic.mod_name"))
                .icon(MinionItems.MINION_ITEM::getDefaultInstance)
                .displayItems(MinionCreativeTab::collectEntries)
                .build();
        PolymerCreativeModeTabUtils.registerPolymerCreativeModeTab(Identifier.fromNamespaceAndPath(Minions.MOD_ID, "main"), group);
    }
}

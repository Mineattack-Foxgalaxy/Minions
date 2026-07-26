package io.github.skippyall.minions.polymer;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import eu.pb4.polymer.core.api.item.PolymerCreativeModeTabUtils;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.polymer.block.AnalogInputBlockOverlay;
import io.github.skippyall.minions.polymer.block.ConnectorBlockOverlay;
import io.github.skippyall.minions.polymer.block.MinionTriggerBlockOverlay;
import io.github.skippyall.minions.polymer.item.ClipboardItemOverlay;
import io.github.skippyall.minions.polymer.item.MinionItemOverlay;
import io.github.skippyall.minions.polymer.item.MinionsBlockItemOverlay;
import io.github.skippyall.minions.polymer.item.SimpleItemOverlay;
import io.github.skippyall.minions.registration.MinionBlocks;
import io.github.skippyall.minions.registration.MinionComponentTypes;
import io.github.skippyall.minions.registration.MinionCreativeTab;
import io.github.skippyall.minions.registration.MinionItems;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class PolymerRegistration {
    public static void register() {
        VersionSync.register();
        PolymerResourcePackUtils.addModAssets(Minions.MOD_ID);

        registerBlockOverlay(MinionBlocks.MINION_TRIGGER, new MinionTriggerBlockOverlay());
        PolymerBlockUtils.registerBlockEntity(MinionBlocks.MINION_TRIGGER_BE_TYPE);
        registerBlockOverlay(MinionBlocks.ANALOG_INPUT, new AnalogInputBlockOverlay());
        registerBlockOverlay(MinionBlocks.TRIGGER_CONNECTOR, new ConnectorBlockOverlay());

        registerItemOverlay(MinionBlocks.MINION_TRIGGER.asItem(), new MinionsBlockItemOverlay(MinionBlocks.MINION_TRIGGER.asItem(), Items.COMPARATOR));
        registerItemOverlay(MinionBlocks.ANALOG_INPUT.asItem(), new MinionsBlockItemOverlay(MinionBlocks.ANALOG_INPUT.asItem(), Items.COMPARATOR));
        registerItemOverlay(MinionBlocks.TRIGGER_CONNECTOR.asItem(), new MinionsBlockItemOverlay(MinionBlocks.TRIGGER_CONNECTOR.asItem(), Items.CHORUS_PLANT));

        registerItemOverlay(MinionItems.REFERENCE_ITEM, new ClipboardItemOverlay());
        registerItemOverlay(MinionItems.MINION_ITEM, new MinionItemOverlay());
        registerSimpleItemOverlay(MinionItems.BASIC_UPGRADE_BASE, Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
        registerSimpleItemOverlay(MinionItems.ADVANCED_UPGRADE_BASE, Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
        registerSimpleItemOverlay(MinionItems.MOVE_MODULE, Items.IRON_BOOTS);
        registerSimpleItemOverlay(MinionItems.ATTACK_MODULE, Items.IRON_PICKAXE);
        registerSimpleItemOverlay(MinionItems.INTERACT_MODULE, Items.LEVER);
        registerSimpleItemOverlay(MinionItems.MOB_SPAWNING_MODULE, Items.SPAWNER);

        PolymerComponent.registerDataComponent(MinionComponentTypes.MINION_DATA, MinionComponentTypes.REFERENCE, MinionComponentTypes.MODULE);

        PolymerCreativeModeTabUtils.registerPolymerCreativeModeTab(Identifier.fromNamespaceAndPath(Minions.MOD_ID, "main"), MinionCreativeTab.group);
    }

    private static void registerBlockOverlay(Block block, PolymerBlock overlay) {
        PolymerBlock.registerOverlay(block, overlay);

        if(overlay instanceof BlockWithElementHolder elementHolderOverlay) {
            BlockWithElementHolder.registerOverlay(block, elementHolderOverlay);
        }
    }

    private static void registerItemOverlay(Item item, PolymerItem overlay) {
        PolymerItem.registerOverlay(item, overlay);
    }

    private static void registerSimpleItemOverlay(Item serverItem, Item polymerItem) {
        registerItemOverlay(serverItem, SimpleItemOverlay.withoutModel(serverItem, polymerItem));
    }
}

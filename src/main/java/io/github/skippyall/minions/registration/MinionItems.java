package io.github.skippyall.minions.registration;

import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import io.github.skippyall.minions.block.miniontrigger.MinionTriggerBlockItem;
import io.github.skippyall.minions.clipboard.ClipboardItem;
import io.github.skippyall.minions.minion.MinionItem;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.module.MinionModule;
import io.github.skippyall.minions.module.SpecialAbility;
import io.github.skippyall.minions.program.instruction.InstructionType;
import java.util.List;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DamageResistant;

import static io.github.skippyall.minions.Minions.MOD_ID;

public class MinionItems {
    public static final TagKey<DamageType> MINION_ITEM_RESISTS = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(MOD_ID, "minion_item_resists"));
    public static final MinionItem MINION_ITEM = registerItem(
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "minion"),
            settings -> new MinionItem(settings.component(DataComponents.DAMAGE_RESISTANT, new DamageResistant(MINION_ITEM_RESISTS)))
    );

    public static final SimplePolymerItem BASIC_UPGRADE_BASE = registerItem(
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "basic_upgrade_base"),
            settings -> new SimplePolymerItem(settings, Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
    );

    public static final SimplePolymerItem ADVANCED_UPGRADE_BASE = registerItem(
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "advanced_upgrade_base"),
            settings -> new SimplePolymerItem(settings, Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
    );


    public static final SimplePolymerItem MOVE_MODULE = registerModule(
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "move_module"),
            Items.IRON_BOOTS,
            List.of(Instructions.WALK, Instructions.WALK_CONTINUOUS, Instructions.TURN, Instructions.TURN_VECTOR)
    );

    public static final SimplePolymerItem ATTACK_MODULE = registerModule(
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "attack_module"),
            Items.IRON_PICKAXE,
            List.of(Instructions.ATTACK, Instructions.MINE_BLOCK)
    );

    public static final SimplePolymerItem INTERACT_MODULE = registerModule(
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "interact_module"),
            Items.LEVER,
            List.of(Instructions.USE)
    );

    public static final SimplePolymerItem MOB_SPAWNING_MODULE = registerModule(
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "mob_spawning_module"),
            Items.SPAWNER,
            List.of(),
            List.of(SpecialAbilities.MOB_SPAWNING)
    );

    public static final PolymerBlockItem MINION_TRIGGER_ITEM =
            registerItem(
                    MinionBlocks.MINION_TRIGGER_ID,
                    settings -> new MinionTriggerBlockItem(MinionBlocks.MINION_TRIGGER_BLOCK, settings, Items.COMPARATOR),
                    new Item.Properties().useBlockDescriptionPrefix()
            );

    public static final ClipboardItem REFERENCE_ITEM = registerItem(ResourceLocation.fromNamespaceAndPath(MOD_ID, "clipboard"), ClipboardItem::new);

    public static <T extends Item> T registerItem(ResourceLocation identifier, Function<Item.Properties, T> constructor, Item.Properties settings) {
        T item = constructor.apply(settings.setId(ResourceKey.create(Registries.ITEM, identifier)));

        MinionCreativeTab.add(item);

        return Registry.register(BuiltInRegistries.ITEM, identifier, item);
    }

    public static <T extends Item> T registerItem(ResourceLocation identifier, Function<Item.Properties, T> constructor) {
        return registerItem(identifier, constructor, new Item.Properties());
    }

    public static SimplePolymerItem registerModule(ResourceLocation identifier, Item vanillaItem, List<InstructionType<MinionRuntime>> instructionTypes, List<SpecialAbility> specialAbilities) {
        return registerItem(
                identifier,
                settings -> new SimplePolymerItem(settings, vanillaItem),
                new Item.Properties().component(MinionComponentTypes.MODULE, new MinionModule(instructionTypes, specialAbilities))
        );
    }

    public static SimplePolymerItem registerModule(ResourceLocation identifier, Item vanillaItem, List<InstructionType<MinionRuntime>> instructionTypes) {
        return registerModule(
                identifier,
                vanillaItem,
                instructionTypes,
                List.of()
        );
    }

    public static void register() {
    }
}

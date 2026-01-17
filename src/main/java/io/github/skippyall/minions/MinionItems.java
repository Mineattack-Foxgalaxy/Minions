package io.github.skippyall.minions;

import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import io.github.skippyall.minions.block.MinionTriggerBlockItem;
import io.github.skippyall.minions.minion.MinionItem;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.module.MinionModule;
import io.github.skippyall.minions.module.MobSpawningAbility;
import io.github.skippyall.minions.module.SpecialAbilities;
import io.github.skippyall.minions.module.SpecialAbility;
import io.github.skippyall.minions.program.instruction.InstructionType;
import io.github.skippyall.minions.program.instruction.Instructions;
import io.github.skippyall.minions.reference.ReferenceItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DamageResistantComponent;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Function;

import static io.github.skippyall.minions.Minions.MOD_ID;

public class MinionItems {
    public static final TagKey<DamageType> MINION_ITEM_RESISTS = TagKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(MOD_ID, "minion_item_resists"));
    public static final MinionItem MINION_ITEM = registerItem(
            Identifier.of(MOD_ID, "minion"),
            settings -> new MinionItem(settings.component(DataComponentTypes.DAMAGE_RESISTANT, new DamageResistantComponent(MINION_ITEM_RESISTS)))
    );

    public static final SimplePolymerItem BASIC_UPGRADE_BASE = registerItem(
            Identifier.of(MOD_ID, "basic_upgrade_base"),
            settings -> new SimplePolymerItem(settings, Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
    );

    public static final SimplePolymerItem ADVANCED_UPGRADE_BASE = registerItem(
            Identifier.of(MOD_ID, "advanced_upgrade_base"),
            settings -> new SimplePolymerItem(settings, Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
    );


    public static final SimplePolymerItem MOVE_MODULE = registerModule(
            Identifier.of(MOD_ID, "move_module"),
            Items.IRON_BOOTS,
            List.of(Instructions.WALK, Instructions.WALK_CONTINUOUS, Instructions.TURN, Instructions.TURN_VECTOR)
    );

    public static final SimplePolymerItem ATTACK_MODULE = registerModule(
            Identifier.of(MOD_ID, "attack_module"),
            Items.IRON_PICKAXE,
            List.of(Instructions.ATTACK, Instructions.MINE_BLOCK)
    );

    public static final SimplePolymerItem INTERACT_MODULE = registerModule(
            Identifier.of(MOD_ID, "interact_module"),
            Items.LEVER,
            List.of(Instructions.USE)
    );

    public static final SimplePolymerItem MOB_SPAWNING_MODULE = registerModule(
            Identifier.of(MOD_ID, "mob_spawning_module"),
            Items.SPAWNER,
            List.of(),
            List.of(SpecialAbilities.MOB_SPAWNING)
    );

    public static final PolymerBlockItem MINION_TRIGGER_ITEM =
            registerItem(
                    MinionBlocks.MINION_TRIGGER_ID,
                    settings -> new MinionTriggerBlockItem(MinionBlocks.MINION_TRIGGER_BLOCK, settings, Items.COMPARATOR)
            );

    public static final ReferenceItem REFERENCE_ITEM = registerItem(Identifier.of(MOD_ID, "reference"), ReferenceItem::new);

    public static <T extends Item> T registerItem(Identifier identifier, Function<Item.Settings, T> constructor, Item.Settings settings) {
        T item = constructor.apply(settings.registryKey(RegistryKey.of(RegistryKeys.ITEM, identifier)));

        MinionCreativeTab.add(item);

        return Registry.register(Registries.ITEM, identifier, item);
    }

    public static <T extends Item> T registerItem(Identifier identifier, Function<Item.Settings, T> constructor) {
        return registerItem(identifier, constructor, new Item.Settings());
    }

    public static SimplePolymerItem registerModule(Identifier identifier, Item vanillaItem, List<InstructionType<MinionRuntime>> instructionTypes, List<SpecialAbility> specialAbilities) {
        return registerItem(
                identifier,
                settings -> new SimplePolymerItem(settings, vanillaItem),
                new Item.Settings().component(MinionModule.COMPONENT_TYPE, new MinionModule(instructionTypes, specialAbilities))
        );
    }

    public static SimplePolymerItem registerModule(Identifier identifier, Item vanillaItem, List<InstructionType<MinionRuntime>> instructionTypes) {
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

package io.github.skippyall.minions.registration;

import io.github.skippyall.minions.minion.MinionItem;
import io.github.skippyall.minions.module.MinionModule;
import io.github.skippyall.minions.module.SpecialAbility;
import io.github.skippyall.minions.program.instruction.InstructionType;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.DamageResistant;

import java.util.List;
import java.util.function.Function;

import static io.github.skippyall.minions.Minions.MOD_ID;

public class MinionItems {
    public static final TagKey<DamageType> MINION_ITEM_RESISTS = TagKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(MOD_ID, "minion_item_resists"));
    public static final MinionItem MINION_ITEM = registerItem(
            Identifier.fromNamespaceAndPath(MOD_ID, "minion"),
            settings -> new MinionItem(settings.delayedComponent(DataComponents.DAMAGE_RESISTANT, context -> new DamageResistant(context.getOrThrow(MINION_ITEM_RESISTS))))
    );

    public static final Item BASIC_UPGRADE_BASE = registerSimpleItem(Identifier.fromNamespaceAndPath(MOD_ID, "basic_upgrade_base"));
    public static final Item ADVANCED_UPGRADE_BASE = registerSimpleItem(Identifier.fromNamespaceAndPath(MOD_ID, "advanced_upgrade_base"));

    public static final Item MOVE_MODULE = registerModule(
            Identifier.fromNamespaceAndPath(MOD_ID, "move_module"),
            List.of(Instructions.WALK, Instructions.WALK_CONTINUOUS, Instructions.TURN, Instructions.TURN_VECTOR)
    );

    public static final Item ATTACK_MODULE = registerModule(
            Identifier.fromNamespaceAndPath(MOD_ID, "attack_module"),
            List.of(Instructions.ATTACK, Instructions.MINE_BLOCK)
    );

    public static final Item INTERACT_MODULE = registerModule(
            Identifier.fromNamespaceAndPath(MOD_ID, "interact_module"),
            List.of(Instructions.USE)
    );

    public static final Item MOB_SPAWNING_MODULE = registerModule(
            Identifier.fromNamespaceAndPath(MOD_ID, "mob_spawning_module"),
            List.of(),
            List.of(SpecialAbilities.MOB_SPAWNING)
    );

    public static final BlockItem MINION_TRIGGER_ITEM = registerItem(
            MinionBlocks.MINION_TRIGGER_ID,
            settings -> new BlockItem(MinionBlocks.MINION_TRIGGER_BLOCK, settings),
            new Item.Properties().useBlockDescriptionPrefix()
    );

    public static final BlockItem ANALOG_INPUT_ITEM = registerItem(
            MinionBlocks.ANALOG_INPUT_BLOCK_ID,
            settings -> new BlockItem(MinionBlocks.ANALOG_INPUT_BLOCK, settings),
            new Item.Properties().useBlockDescriptionPrefix()
    );

    public static final BlockItem CONNECTOR_ITEM = registerItem(
            MinionBlocks.CONNECTOR_ID,
            settings -> new BlockItem(MinionBlocks.CONNECTOR, settings),
            new Item.Properties().useBlockDescriptionPrefix()
    );

    public static final Item REFERENCE_ITEM = registerItem(Identifier.fromNamespaceAndPath(MOD_ID, "clipboard"), Item::new);

    public static <T extends Item> T registerItem(Identifier identifier, Function<Item.Properties, T> constructor, Item.Properties settings) {
        T item = constructor.apply(settings.setId(ResourceKey.create(Registries.ITEM, identifier)));

        MinionCreativeTab.add(item);

        return Registry.register(BuiltInRegistries.ITEM, identifier, item);
    }

    public static <T extends Item> T registerItem(Identifier identifier, Function<Item.Properties, T> constructor) {
        return registerItem(identifier, constructor, new Item.Properties());
    }

    public static Item registerSimpleItem(Identifier identifier) {
        return registerItem(identifier, Item::new);
    }

    public static Item registerModule(Identifier identifier, List<InstructionType> instructionTypes, List<SpecialAbility> specialAbilities) {
        return registerItem(
                identifier,
                Item::new,
                new Item.Properties().component(MinionComponentTypes.MODULE, new MinionModule(instructionTypes, specialAbilities))
        );
    }

    public static Item registerModule(Identifier identifier, List<InstructionType> instructionTypes) {
        return registerModule(
                identifier,
                instructionTypes,
                List.of()
        );
    }

    public static void register() {
    }
}

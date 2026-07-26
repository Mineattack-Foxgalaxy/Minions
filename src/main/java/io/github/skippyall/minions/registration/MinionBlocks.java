package io.github.skippyall.minions.registration;

import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.block.ConnectorBlock;
import io.github.skippyall.minions.block.input.ValueProvider;
import io.github.skippyall.minions.block.miniontrigger.MinionTriggerBlock;
import io.github.skippyall.minions.block.miniontrigger.MinionTriggerBlockEntity;
import io.github.skippyall.minions.program.value.TypedValue;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Function;

public class MinionBlocks {
    public static final MinionTriggerBlock MINION_TRIGGER = registerBlockWithItem(
            "minion_trigger",
            MinionTriggerBlock::new,
            BlockBehaviour.Properties.of()
                    .noOcclusion()
                    .instabreak()
                    .sound(SoundType.STONE)
                    .pushReaction(PushReaction.DESTROY)
    );
    public static final BlockEntityType<MinionTriggerBlockEntity> MINION_TRIGGER_BE_TYPE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Minions.id("minion_trigger"),
            FabricBlockEntityTypeBuilder.create(MinionTriggerBlockEntity::new, MINION_TRIGGER).build()
    );

    public static final Block ANALOG_INPUT = registerBlockWithItem(
            "analog_input",
            Block::new,
            BlockBehaviour.Properties.of()
    );

    public static final Block TRIGGER_CONNECTOR = registerBlockWithItem(
            "trigger_connector",
            ConnectorBlock::new,
            BlockBehaviour.Properties.of()
    );


    public static <T extends Block> T registerBlockWithItem(
            String id,
            Function<BlockBehaviour.Properties, T> constructor,
            BlockBehaviour.Properties properties
    ) {
        return registerBlockWithItem(id, constructor, properties, new Item.Properties());
    }

    public static <T extends Block> T registerBlockWithItem(
            String id,
            Function<BlockBehaviour.Properties, T> constructor,
            BlockBehaviour.Properties properties,
            Item.Properties itemProperties
    ) {
        T block = registerBlock(id, constructor, properties);
        MinionItems.registerItem(
                Minions.id(id),
                newItemProperties -> new BlockItem(block, newItemProperties),
                itemProperties.useBlockDescriptionPrefix()
        );
        return block;
    }

    public static <T extends Block> T registerBlock(
            String id,
            Function<BlockBehaviour.Properties, T> constructor,
            BlockBehaviour.Properties properties
    ) {
        properties.setId(ResourceKey.create(Registries.BLOCK, Minions.id(id)));
        return Registry.register(BuiltInRegistries.BLOCK, id, constructor.apply(properties));
    }

    public static void register() {
        ValueProvider.registerBlock(MinionBlocks.ANALOG_INPUT, (level, pos, state, blockEntity, direction) -> new TypedValue<>((long) level.getBestNeighborSignal(pos), ValueTypes.LONG));
    }
}

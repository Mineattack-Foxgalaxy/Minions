package io.github.skippyall.minions.registration;

import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.block.input.AnalogInputBlock;
import io.github.skippyall.minions.block.miniontrigger.MinionTriggerBlock;
import io.github.skippyall.minions.block.miniontrigger.MinionTriggerBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

public class MinionBlocks {
    public static final Identifier MINION_TRIGGER_ID = Identifier.fromNamespaceAndPath(Minions.MOD_ID, "minion_trigger");
    public static final MinionTriggerBlock MINION_TRIGGER_BLOCK = Registry.register(
            BuiltInRegistries.BLOCK,
            MINION_TRIGGER_ID,
            new MinionTriggerBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, MINION_TRIGGER_ID))
                    .noOcclusion()
                    .instabreak()
                    .sound(SoundType.STONE)
                    .pushReaction(PushReaction.DESTROY)
            )
    );
    public static final BlockEntityType<MinionTriggerBlockEntity> MINION_TRIGGER_BE_TYPE =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    MINION_TRIGGER_ID,
                    FabricBlockEntityTypeBuilder.create(MinionTriggerBlockEntity::new, MINION_TRIGGER_BLOCK).build()
            );


    public static final Identifier ANALOG_INPUT_BLOCK_ID = Identifier.fromNamespaceAndPath(Minions.MOD_ID, "analog_input");
    public static final AnalogInputBlock ANALOG_INPUT_BLOCK = Registry.register(
            BuiltInRegistries.BLOCK,
            ANALOG_INPUT_BLOCK_ID,
            new AnalogInputBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, ANALOG_INPUT_BLOCK_ID))
            )
    );

    public static void register() {
        PolymerBlockUtils.registerBlockEntity(MINION_TRIGGER_BE_TYPE);
    }
}

package io.github.skippyall.minions;

import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import io.github.skippyall.minions.block.MinionTriggerBlock;
import io.github.skippyall.minions.block.MinionTriggerBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class MinionBlocks {
    public static final Identifier MINION_TRIGGER_ID = Identifier.of(Minions.MOD_ID, "minion_trigger");
    public static final MinionTriggerBlock MINION_TRIGGER_BLOCK = Registry.register(
            Registries.BLOCK,
            MINION_TRIGGER_ID,
            new MinionTriggerBlock(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, MINION_TRIGGER_ID))
                    .nonOpaque()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STONE)
                    .pistonBehavior(PistonBehavior.DESTROY)
            )
    );
    public static final BlockEntityType<MinionTriggerBlockEntity> MINION_TRIGGER_BE_TYPE =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    MINION_TRIGGER_ID,
                    FabricBlockEntityTypeBuilder.create(MinionTriggerBlockEntity::new, MINION_TRIGGER_BLOCK).build()
            );

    public static void register() {
        PolymerBlockUtils.registerBlockEntity(MINION_TRIGGER_BE_TYPE);
    }
}

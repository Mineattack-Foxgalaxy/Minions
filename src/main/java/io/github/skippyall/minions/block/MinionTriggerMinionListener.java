package io.github.skippyall.minions.block;

import io.github.skippyall.minions.MinionBlocks;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.ConfiguredInstructionListener;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class MinionTriggerMinionListener extends BlockEntityMinionListener<MinionTriggerBlockEntity> implements ConfiguredInstructionListener {
    boolean registeredListener;
    String instructionName;

    public MinionTriggerMinionListener(RegistryKey<World> worldKey, BlockPos pos, String instructionName, boolean registeredListener) {
        super(worldKey, pos, MinionBlocks.MINION_TRIGGER_BE_TYPE);
        this.instructionName = instructionName;
        this.registeredListener = registeredListener;
    }

    @Override
    public void onMinionSpawn(MinionFakePlayer minion) {
        if(!registeredListener) {
            minion.getInstructionManager().getInstruction(instructionName).addListener(this);
        }
    }

    @Override
    public void onMinionRemove(MinionFakePlayer minion) {
    }
}

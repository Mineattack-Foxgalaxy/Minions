package io.github.skippyall.minions.block;

import io.github.skippyall.minions.MinionRegistration;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class MinionTriggerBlockEntity extends BlockEntity {
    private UUID minionUuid;
    private String instructionName = "";

    public MinionTriggerBlockEntity(BlockPos pos, BlockState state) {
        super(MinionRegistration.MINION_TRIGGER_BE_TYPE, pos, state);
    }

    public void updatePower() {
        boolean powered = getCachedState().get(MinionTriggerBlock.POWERED);
        ConfiguredInstruction<MinionRuntime> instruction = getInstruction();

        if(instruction != null) {
            if(powered) {
                instruction.run(getMinion().getInstructionManager());
            } else {
                instruction.stop(getMinion().getInstructionManager());
            }
        }
    }

    public int getComparatorOutput() {
        ConfiguredInstruction<MinionRuntime> instruction = getInstruction();
        if(instruction != null && instruction.isRunning()) {
            return 15;
        }
        return 0;
    }

    public MinionFakePlayer getMinion() {
        if(minionUuid != null && world != null && world.getPlayerByUuid(minionUuid) instanceof MinionFakePlayer minion) {
            return minion;
        }
        return null;
    }

    public ConfiguredInstruction<MinionRuntime> getInstruction() {
        MinionFakePlayer minion = getMinion();
        if(minion == null) {
            return null;
        }

        return minion.getInstructionManager().getInstruction(instructionName);
    }

    @Override
    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
        super.onBlockReplaced(pos, oldState);
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
    }

    @Override
    protected void readData(ReadView view) {
        minionUuid = view.read("minionUuid", Uuids.CODEC).orElse(null);
        instructionName = view.getString("instructionName", "");
    }

    @Override
    protected void writeData(WriteView view) {
        if(minionUuid != null) {
            view.put("minionUuid", Uuids.CODEC, minionUuid);
        }
        view.putString("instructionName", instructionName);
    }
}

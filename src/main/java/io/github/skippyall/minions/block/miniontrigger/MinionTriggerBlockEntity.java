package io.github.skippyall.minions.block.miniontrigger;

import io.github.skippyall.minions.block.instruction_bound.InstructionBoundBlockEntity;
import io.github.skippyall.minions.listener.BlockEntityMinionListener;
import io.github.skippyall.minions.registration.MinionBlocks;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.UUID;

public class MinionTriggerBlockEntity extends InstructionBoundBlockEntity<MinionTriggerMinionListener> {
    public MinionTriggerBlockEntity(BlockPos pos, BlockState state) {
        super(MinionBlocks.MINION_TRIGGER_BE_TYPE, pos, state);
    }

    @Override
    protected MinionTriggerMinionListener createListener() {
        return new MinionTriggerMinionListener(world.getRegistryKey(), pos, minionUuid, instructionName);
    }

    @Override
    protected Class<MinionTriggerMinionListener> getListenerClass() {
        return MinionTriggerMinionListener.class;
    }

    public void updatePower() {
        boolean powered = getCachedState().get(MinionTriggerBlock.POWERED);

        MinionTriggerMinionListener listener = getListener();
        if(listener != null) {
            listener.incomingPowerCache = powered;
        }

        getMinion().ifPresent(minion -> {
            getInstruction().ifPresent(instruction -> {
                if(powered) {
                    instruction.run(minion.getInstructionManager());
                } else {
                    instruction.stop(minion.getInstructionManager());
                }
            });
        });
    }

    public int getComparatorOutput() {
        MinionTriggerMinionListener listener = getListener();
        if(listener != null && listener.runningCache) {
            return 15;
        }
        return 0;
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

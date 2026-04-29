package io.github.skippyall.minions.block.miniontrigger;

import io.github.skippyall.minions.block.instruction_bound.InstructionBoundBlockEntity;
import io.github.skippyall.minions.registration.MinionBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class MinionTriggerBlockEntity extends InstructionBoundBlockEntity<MinionTriggerMinionListener> {
    public MinionTriggerBlockEntity(BlockPos pos, BlockState state) {
        super(MinionBlocks.MINION_TRIGGER_BE_TYPE, pos, state);
    }

    @Override
    protected MinionTriggerMinionListener createListener() {
        return new MinionTriggerMinionListener(level.dimension(), worldPosition, minionUuid, instructionName);
    }

    @Override
    protected Class<MinionTriggerMinionListener> getListenerClass() {
        return MinionTriggerMinionListener.class;
    }

    public void updatePower() {
        boolean powered = getBlockState().getValue(MinionTriggerBlock.POWERED);

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
    protected void loadAdditional(ValueInput view) {
        minionUuid = view.read("minionUuid", UUIDUtil.AUTHLIB_CODEC).orElse(null);
        instructionName = view.getStringOr("instructionName", "");
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        if(minionUuid != null) {
            view.store("minionUuid", UUIDUtil.AUTHLIB_CODEC, minionUuid);
        }
        view.putString("instructionName", instructionName);
    }
}

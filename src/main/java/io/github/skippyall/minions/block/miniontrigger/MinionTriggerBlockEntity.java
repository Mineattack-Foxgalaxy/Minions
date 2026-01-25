package io.github.skippyall.minions.block.miniontrigger;

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

public class MinionTriggerBlockEntity extends BlockEntity {
    private UUID minionUuid;
    private String instructionName = "";

    public MinionTriggerBlockEntity(BlockPos pos, BlockState state) {
        super(MinionBlocks.MINION_TRIGGER_BE_TYPE, pos, state);
    }

    public void removeListener() {
        MinionTriggerMinionListener.removeListener(world, pos, minionUuid, instructionName);
    }

    public void addListener() {
        MinionTriggerMinionListener.addListener(world, pos, minionUuid, instructionName);
    }

    public void setInstruction(UUID minionUuid, String instructionName) {
        removeListener();
        this.minionUuid = minionUuid;
        this.instructionName = instructionName;
        addListener();
        markDirty();
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

    public Optional<MinionFakePlayer> getMinion() {
        if(minionUuid != null && world != null && world.getPlayerByUuid(minionUuid) instanceof MinionFakePlayer minion) {
            return Optional.of(minion);
        }
        return Optional.empty();
    }

    public UUID getMinionUuid() {
        return minionUuid;
    }

    public String getInstructionName() {
        return instructionName;
    }

    public Optional<ConfiguredInstruction<MinionRuntime>> getInstruction(MinionFakePlayer minion) {
        return Optional.ofNullable(minion.getInstructionManager().getInstruction(instructionName));
    }

    public Optional<ConfiguredInstruction<MinionRuntime>> getInstruction() {
        return getMinion().flatMap(this::getInstruction);
    }

    public MinionTriggerMinionListener getListener() {
        return BlockEntityMinionListener.getListener(world, pos, minionUuid, MinionTriggerMinionListener.class);
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

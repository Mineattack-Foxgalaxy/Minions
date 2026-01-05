package io.github.skippyall.minions.block;

import io.github.skippyall.minions.MinionBlocks;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;

public class MinionTriggerBlockEntity extends BlockEntity {
    private UUID minionUuid;
    private String instructionName = "";

    private boolean first = true;
    private boolean runningCache = false;

    public MinionTriggerBlockEntity(BlockPos pos, BlockState state) {
        super(MinionBlocks.MINION_TRIGGER_BE_TYPE, pos, state);
    }

    public void setInstruction(UUID minionUuid, String instructionName) {
        this.minionUuid = minionUuid;
        this.instructionName = instructionName;
        markDirty();
    }

    public static void tick(World world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if(!(blockEntity instanceof MinionTriggerBlockEntity triggerBlockEntity)) {
            return;
        }
        if(triggerBlockEntity.first) {
            triggerBlockEntity.first = false;
            world.updateComparators(pos, MinionBlocks.MINION_TRIGGER_BLOCK);
            triggerBlockEntity.runningCache = triggerBlockEntity.getInstruction().map(ConfiguredInstruction::isRunning).orElse(false);
        } else {
            boolean isRunning = triggerBlockEntity.getInstruction().map(ConfiguredInstruction::isRunning).orElse(false);
            if (isRunning != triggerBlockEntity.runningCache) {
                world.updateComparators(pos, MinionBlocks.MINION_TRIGGER_BLOCK);
                triggerBlockEntity.runningCache = isRunning;
            }
        }
    }

    public void updatePower() {
        boolean powered = getCachedState().get(MinionTriggerBlock.POWERED);
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
        Optional<ConfiguredInstruction<MinionRuntime>> instruction = getInstruction();
        if(instruction.isPresent() && instruction.get().isRunning()) {
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

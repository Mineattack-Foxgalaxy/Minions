package io.github.skippyall.minions.block.instruction_bound;

import io.github.skippyall.minions.listener.BlockEntityMinionListener;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.UUID;

public abstract class InstructionBoundBlockEntity<L extends BlockEntityMinionListener<?>> extends BlockEntity {
    protected UUID minionUuid;
    protected String instructionName = "";

    public InstructionBoundBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected abstract L createListener();

    protected abstract Class<L> getListenerClass();

    public void removeListener() {
        if(level instanceof ServerLevel serverWorld) {
            L listener = getListener();
            listener.remove(serverWorld.getServer());
        }
    }

    public void addListener() {
        if(level instanceof ServerLevel serverWorld) {
            L listener = createListener();
            listener.add(serverWorld.getServer());
        }
    }

    public void setInstruction(UUID minionUuid, String instructionName) {
        removeListener();
        this.minionUuid = minionUuid;
        this.instructionName = instructionName;
        addListener();
        setChanged();
    }

    public Optional<MinionFakePlayer> getMinion() {
        if(minionUuid != null && level != null && level.getPlayerByUUID(minionUuid) instanceof MinionFakePlayer minion) {
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

    public L getListener() {
        return BlockEntityMinionListener.getListener(level, worldPosition, minionUuid, getListenerClass());
    }
}

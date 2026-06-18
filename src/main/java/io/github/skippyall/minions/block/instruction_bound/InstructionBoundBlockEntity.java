package io.github.skippyall.minions.block.instruction_bound;

import io.github.skippyall.minions.GlobalInstructionManager;
import io.github.skippyall.minions.program.instruction.ExecutingInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/*public abstract class InstructionBoundBlockEntity extends BlockEntity {
    protected int instructionId;

    public InstructionBoundBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected abstract ExecutingInstruction.Listener createListener();

    public void removeListener() {
        if(level instanceof ServerLevel serverWorld) {
            L listener = getListener();
            if(listener != null) {
                listener.remove(serverWorld.getServer());
            }
        }
    }

    public void addListener() {
        if(level instanceof ServerLevel serverWorld) {
            L listener = createListener();
            listener.add(serverWorld.getServer());
        }
    }

    public void setInstruction(int instructionId) {
        removeListener();
        this.instructionId = instructionId;
        addListener();
        setChanged();
    }

    public int getInstructionId() {
        return instructionId;
    }

    public @Nullable ExecutingInstruction<?> getInstruction() {
        if(level != null) {
            MinecraftServer server = level.getServer();
            if(server != null) {
                return GlobalInstructionManager.get(server).getInstruction(instructionId);
            }
        }
        return null;
    }
}*/

package io.github.skippyall.minions.minion;

import io.github.skippyall.minions.GlobalInstructionManager;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.ExecutionContext;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.instruction.ExecutingInstruction;
import io.github.skippyall.minions.program.instruction.InstructionType;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.MinecraftServer;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.OptionalInt;

public class MinionRuntime implements InstructionRuntime {
    public static final ExecutionContext.Key<MinionFakePlayer> MINION_KEY = new ExecutionContext.Key<>(Minions.id("minion"));

    private final MinionFakePlayer minion;
    private ExecutionContext context;

    public MinionRuntime(MinionFakePlayer minion) {
        this.minion = minion;
        this.context = getContext();
    }

    public MinionFakePlayer getMinion() {
        return minion;
    }

    @Override
    public MinecraftServer getServer() {
        return minion.getServer();
    }

    public void onLoad() {
        for(ExecutingInstruction instruction : getInstructions()) {
            instruction.onLoaded(this);
        }
    }

    public void onUnload() {
        for(ExecutingInstruction instruction : getInstructions()) {
            instruction.onUnloaded();
        }
    }

    public void tick() {
        for(ExecutingInstruction instruction : getInstructions()) {
            instruction.tick(context);
        }
        removeStoppedInstructions();
    }

    public void disableInstructionType(InstructionType instructionType) {
        for(ExecutingInstruction instruction : getInstructions()) {
            if(instruction.getInstructionType() == instructionType) {
                instruction.stop(context);
            }
        }
        removeStoppedInstructions();
    }

    public ExecutionContext getContext() {
        ExecutionContext context = new ExecutionContext();
        context.put(MINION_KEY, minion);
        return context;
    }

    public void enableInstructionType(InstructionType instructionType) {}

    @Override
    public boolean isInstructionEnabled(InstructionType type) {
        return minion.getModuleInventory().hasInstruction(type);
    }

    @Override
    public OptionalInt run(ConfiguredInstruction instruction) {
        return instruction.run(context, this);
    }

    @Override
    public int addInstruction(ExecutingInstruction executingInstruction) {
        int id = GlobalInstructionManager.get(minion.getServer()).addInstruction(minion.getUUID(), executingInstruction);
        executingInstruction.onLoaded(this);
        return id;
    }

    @Nullable
    public ExecutingInstruction getInstruction(int id) {
        return GlobalInstructionManager.get(minion.getServer()).getInstruction(minion.getUUID(), id);
    }

    public Int2ObjectMap<ExecutingInstruction> getInstructionMap() {
        return GlobalInstructionManager.get(minion.getServer()).getInstructions(minion.getUUID());
    }

    public Collection<ExecutingInstruction> getInstructions() {
        return getInstructionMap().values();
    }

    private void removeStoppedInstructions() {
        getInstructions().removeIf(instruction -> {
            return instruction.getState() == ExecutingInstruction.State.STOPPED;
        });
    }
}

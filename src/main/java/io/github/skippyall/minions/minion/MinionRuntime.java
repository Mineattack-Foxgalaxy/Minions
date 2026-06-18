package io.github.skippyall.minions.minion;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.GlobalInstructionManager;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.consumer.ValueConsumerType;
import io.github.skippyall.minions.program.instruction.ExecutingInstruction;
import io.github.skippyall.minions.program.instruction.InstructionType;
import io.github.skippyall.minions.program.supplier.ValueSupplierType;
import io.github.skippyall.minions.registration.MinionRegistries;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class MinionRuntime implements InstructionRuntime<MinionRuntime> {
    private final MinionFakePlayer minion;
    private final IntSet executingInstructions = new IntRBTreeSet();

    public MinionRuntime(MinionFakePlayer minion) {
        this.minion = minion;
    }

    public MinionFakePlayer getMinion() {
        return minion;
    }

    @Override
    public MinecraftServer getServer() {
        return minion.getServer();
    }

    public void tick() {
        for(int id : executingInstructions) {
            ExecutingInstruction<MinionRuntime> instruction = getInstruction(id);
            if(instruction != null) {
                instruction.tick(this);
            }
        }
        removeStoppedInstructions();
    }

    public void disableInstructionType(InstructionType<MinionRuntime> instructionType) {
        for(int id : executingInstructions) {
            ExecutingInstruction<MinionRuntime> instruction = getInstruction(id);
            if(instruction != null && instruction.getInstructionType() == instructionType) {
                instruction.stop(this);
            }
        }
        removeStoppedInstructions();
    }

    public void enableInstructionType(InstructionType<MinionRuntime> instructionType) {}

    @Override
    public boolean isInstructionEnabled(InstructionType<MinionRuntime> type) {
        return minion.getModuleInventory().hasInstruction(type);
    }

    @Override
    public int addInstruction(ExecutingInstruction<MinionRuntime> executingInstruction) {
        int id = GlobalInstructionManager.get(minion.getServer()).addInstruction(executingInstruction);
        executingInstructions.add(id);
        return id;
    }

    @Nullable
    public ExecutingInstruction<MinionRuntime> getInstruction(int id) {
        //noinspection unchecked
        return (ExecutingInstruction<MinionRuntime>) GlobalInstructionManager.get(minion.getServer()).getInstruction(id);
    }

    private void removeStoppedInstructions() {
        executingInstructions.removeIf(id -> {
            ExecutingInstruction<MinionRuntime> instruction = getInstruction(id);
            return instruction == null || instruction.getState() == ExecutingInstruction.State.STOPPED;
        });
    }

    public void save(ValueOutput view) {
        ValueOutput.TypedOutputList<Integer> list = view.list("executingInstructions", Codec.INT);
        for (int id : executingInstructions) {
            list.add(id);
        }
    }

    public void load(ValueInput view) {
        Optional<ValueInput.TypedInputList<Integer>> list = view.list("executingInstructions", Codec.INT);
        if(list.isPresent()) {
            for (int id : list.get()) {
                executingInstructions.add(id);
            }
        }
    }

    @Override
    public Registry<ValueConsumerType<MinionRuntime>> getValueConsumerTypeRegistry() {
        return MinionRegistries.VALUE_CONSUMER_TYPES;
    }
}

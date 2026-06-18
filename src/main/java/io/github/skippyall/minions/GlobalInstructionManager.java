package io.github.skippyall.minions;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.program.instruction.ExecutingInstruction;
import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GlobalInstructionManager extends SavedData {
    public static final Codec<GlobalInstructionManager> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.mapPair(
                            Codec.INT.fieldOf("id"),
                            ExecutingInstruction.MAP_CODEC
                    ).codec().listOf().fieldOf("instructions").forGetter(GlobalInstructionManager::createInstructionList),
                    Codec.INT.fieldOf("currentId").forGetter(m -> m.currentId)
            ).apply(instance, GlobalInstructionManager::new));

    @SuppressWarnings("DataFlowIssue") //Allowed by Fabric API
    private static final SavedDataType<GlobalInstructionManager> TYPE = new SavedDataType<>(Minions.id("global_instruction_manager"), GlobalInstructionManager::new, CODEC, null);

    private final Int2ObjectRBTreeMap<ExecutingInstruction<?>> executingInstructions = new Int2ObjectRBTreeMap<>();
    private int currentId;

    public GlobalInstructionManager() {
    }

    private GlobalInstructionManager(List<Pair<Integer, ExecutingInstruction<?>>> executingInstructions, int currentId) {
        for(Pair<Integer, ExecutingInstruction<?>> pair : executingInstructions) {
            this.executingInstructions.put(pair.getFirst().intValue(), pair.getSecond());
        }
    }

    public static GlobalInstructionManager get(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
    }

    private List<Pair<Integer, ExecutingInstruction<?>>> createInstructionList() {
        List<Pair<Integer, ExecutingInstruction<?>>> list = new ArrayList<>();
        executingInstructions.forEach((id, instruction) -> list.add(new Pair<>(id, instruction)));
        return list;
    }

    public int addInstruction(ExecutingInstruction<?> executingInstruction) {
        currentId++;
        executingInstructions.put(currentId, executingInstruction);
        return currentId;
    }

    @Nullable
    public ExecutingInstruction<?> getInstruction(int id) {
        return executingInstructions.get(id);
    }
}

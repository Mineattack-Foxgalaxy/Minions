package io.github.skippyall.minions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.program.instruction.ExecutingInstruction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GlobalInstructionManager extends SavedData {
    public static final Codec<GlobalInstructionManager> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    InstructionEntry.CODEC.listOf().fieldOf("instructions").forGetter(GlobalInstructionManager::createInstructionList),
                    Codec.INT.fieldOf("currentId").forGetter(m -> m.currentId)
            ).apply(instance, GlobalInstructionManager::new));

    @SuppressWarnings("DataFlowIssue") //Allowed by Fabric API
    private static final SavedDataType<GlobalInstructionManager> TYPE = new SavedDataType<>(Minions.id("global_instruction_manager"), GlobalInstructionManager::new, CODEC, null);

    private final Map<UUID, Int2ObjectMap<ExecutingInstruction>> instructions = new HashMap<>();
    private int currentId;

    public GlobalInstructionManager() {
    }

    private GlobalInstructionManager(List<InstructionEntry> executingInstructions, int currentId) {
        for(InstructionEntry entry : executingInstructions) {
            addInstruction(entry.runtime(), entry.id(), entry.instruction());
        }
    }

    public static GlobalInstructionManager get(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
    }

    private List<InstructionEntry> createInstructionList() {
        List<InstructionEntry> list = new ArrayList<>();
        instructions.forEach((runtimeId, instructionMap) ->
                instructionMap.forEach((id, instruction) -> list.add(new InstructionEntry(runtimeId, id, instruction)))
        );
        return list;
    }

    public int addInstruction(UUID runtimeId, ExecutingInstruction executingInstruction) {
        currentId++;
        addInstruction(runtimeId, currentId, executingInstruction);
        return currentId;
    }

    private void addInstruction(UUID runtimeId, int id, ExecutingInstruction executingInstruction) {
        instructions.computeIfAbsent(runtimeId, _ -> new Int2ObjectRBTreeMap<>()).put(currentId, executingInstruction);
    }

    public Int2ObjectMap<ExecutingInstruction> getInstructions(UUID runtimeId) {
        if(instructions.containsKey(runtimeId)) {
            return instructions.get(runtimeId);
        } else {
            return Int2ObjectMaps.emptyMap();
        }
    }

    @Nullable
    public ExecutingInstruction getInstruction(UUID runtimeId, int id) {
        return instructions.getOrDefault(runtimeId, Int2ObjectMaps.emptyMap()).get(id);
    }

    private record InstructionEntry(UUID runtime, int id, ExecutingInstruction instruction) {
        private static final Codec<InstructionEntry> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        UUIDUtil.CODEC.fieldOf("runtime").forGetter(InstructionEntry::runtime),
                        Codec.INT.fieldOf("id").forGetter(InstructionEntry::id),
                        ExecutingInstruction.MAP_CODEC.forGetter(InstructionEntry::instruction)
                ).apply(instance, InstructionEntry::new));
    }
}

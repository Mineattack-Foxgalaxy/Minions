package io.github.skippyall.minions.minion;

import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.instruction.InstructionType;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class MinionInstructionManager {
    private final MinionFakePlayer minion;
    private final Map<String, ConfiguredInstruction<?>> configuredInstructions = new HashMap<>();

    public MinionInstructionManager(MinionFakePlayer minion) {
        this.minion = minion;
    }

    public void tick() {
        for (ConfiguredInstruction<?> instruction : configuredInstructions.values()) {
            instruction.tick(minion);
        }
    }

    public Set<String> getInstructionNames() {
        return configuredInstructions.keySet();
    }

    public <T> ConfiguredInstruction<T> createInstruction(String name, InstructionType<T> instructionType) {
        ConfiguredInstruction<T> instruction = new ConfiguredInstruction<>(instructionType, name);
        configuredInstructions.put(name, instruction);
        return instruction;
    }

    public void removeInstruction(String name) {
        ConfiguredInstruction<?> instruction = getInstruction(name);
        instruction.stop(minion);
        configuredInstructions.remove(name);
    }

    public ConfiguredInstruction<?> getInstruction(String name) {
        return configuredInstructions.get(name);
    }

    public boolean hasInstruction(String name) {
        return configuredInstructions.containsKey(name);
    }

    public void save(WriteView view) {
        WriteView.ListView list = view.getList("configuredInstructions");
        for (Map.Entry<String, ConfiguredInstruction<?>> instruction : configuredInstructions.entrySet()) {
            WriteView inner = list.add();
            inner.putString("name", instruction.getKey());
            instruction.getValue().save(inner, minion);
        }
    }

    public void load(ReadView view) {
        ReadView.ListReadView list = view.getListReadView("configuredInstructions");
        for (ReadView inner : list) {
            Optional<String> name = inner.getOptionalString("name");
            if(name.isEmpty()) {
                Minions.LOGGER.error("Tried deserializing configured instruction without a name of minion \"{}\":", minion.getGameProfile().getName());
                continue;
            }

            try {
                ConfiguredInstruction<?> instruction = ConfiguredInstruction.load(inner, minion, name.get());
                configuredInstructions.put(name.get(), instruction);
            } catch (Exception e) {
                Minions.LOGGER.error("Could not deserialize configured instruction \"{}\" of minion \"{}\":", name.get(), minion.getGameProfile().getName(), e);
            }
        }
    }
}

package io.github.skippyall.minions.minion;

import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.supplier.ValueSupplierType;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.instruction.InstructionType;
import io.github.skippyall.minions.program.consumer.ValueConsumerType;
import net.minecraft.registry.Registry;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class MinionRuntime implements InstructionRuntime<MinionRuntime> {
    private final MinionFakePlayer minion;
    private final Map<String, ConfiguredInstruction<MinionRuntime>> configuredInstructions = new HashMap<>();

    public MinionRuntime(MinionFakePlayer minion) {
        this.minion = minion;
    }

    public MinionFakePlayer getMinion() {
        return minion;
    }

    public void tick() {
        for (ConfiguredInstruction<MinionRuntime> instruction : configuredInstructions.values()) {
            instruction.tick(this);
        }
    }

    public void disableInstructionType(InstructionType<MinionRuntime> instructionType) {
        updatePausedStatus(instructionType);
    }

    public void enableInstructionType(InstructionType<MinionRuntime> instructionType) {
        updatePausedStatus(instructionType);
    }

    public void updatePausedStatus(InstructionType<MinionRuntime> instructionType) {
        for(ConfiguredInstruction<MinionRuntime> instruction : configuredInstructions.values()) {
            if(instruction.getInstruction() == instructionType) {
                instruction.updatePauseStatus(this);
            }
        }
    }

    @Override
    public boolean isInstructionEnabled(InstructionType<MinionRuntime> type) {
        return minion.getModuleInventory().hasInstruction(type);
    }

    public Set<String> getInstructionNames() {
        return configuredInstructions.keySet();
    }

    public ConfiguredInstruction<MinionRuntime> createInstruction(String name, InstructionType<MinionRuntime> instructionType) {
        if(configuredInstructions.containsKey(name)) {
            return null;
        }

        ConfiguredInstruction<MinionRuntime> instruction = new ConfiguredInstruction<>(instructionType);
        configuredInstructions.put(name, instruction);
        minion.forEachMinionListener(listener -> listener.onInstructionsUpdate(minion));
        return instruction;
    }

    public void removeInstruction(String name) {
        ConfiguredInstruction<MinionRuntime> instruction = getInstruction(name);
        instruction.stop(this);
        configuredInstructions.remove(name);

        instruction.onInstructionRemove();
        minion.forEachMinionListener(listener -> listener.onInstructionsUpdate(minion));
    }

    public ConfiguredInstruction<MinionRuntime> getInstruction(String name) {
        return configuredInstructions.get(name);
    }

    public boolean hasInstruction(String name) {
        return configuredInstructions.containsKey(name);
    }

    public void setInstructionName(String oldName, String newName) {
        if(!configuredInstructions.containsKey(newName) && configuredInstructions.containsKey(oldName)) {
            ConfiguredInstruction<MinionRuntime> instruction = configuredInstructions.get(oldName);
            configuredInstructions.remove(oldName);
            configuredInstructions.put(newName, instruction);


            minion.forEachMinionListener(minionListener -> {
                minionListener.onInstructionRename(minion, instruction, oldName, newName);
                minionListener.onInstructionsUpdate(minion);
            });
        }
    }

    public void save(WriteView view) {
        WriteView.ListView list = view.getList("configuredInstructions");
        for (Map.Entry<String, ConfiguredInstruction<MinionRuntime>> instruction : configuredInstructions.entrySet()) {
            WriteView inner = list.add();
            inner.putString("name", instruction.getKey());
            instruction.getValue().save(inner, this);
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
                ConfiguredInstruction<MinionRuntime> instruction = ConfiguredInstruction.load(inner, this);
                configuredInstructions.put(name.get(), instruction);
            } catch (Exception e) {
                Minions.LOGGER.error("Could not deserialize configured instruction \"{}\" of minion \"{}\":", name.get(), minion.getGameProfile().getName(), e);
            }
        }
    }

    @Override
    public Registry<ValueSupplierType<MinionRuntime>> getArgumentTypeRegistry() {
        return MinionRegistries.VALUE_SUPPLIER_TYPES;
    }

    @Override
    public Registry<InstructionType<MinionRuntime>> getInstructionTypeRegistry() {
        return MinionRegistries.INSTRUCTION_TYPES;
    }

    @Override
    public Registry<ValueConsumerType<MinionRuntime>> getValueConsumerTypeRegistry() {
        return MinionRegistries.VALUE_CONSUMER_TYPES;
    }
}

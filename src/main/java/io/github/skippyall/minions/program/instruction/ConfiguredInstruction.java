package io.github.skippyall.minions.program.instruction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.listener.ListenerManager;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.handler.Parameter;
import io.github.skippyall.minions.program.handler.ParameterValueList;
import io.github.skippyall.minions.program.handler.consumer.ValueConsumerList;
import io.github.skippyall.minions.program.handler.supplier.ValueSupplierList;
import io.github.skippyall.minions.registration.MinionRegistries;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

/**
 * Holds an instruction and configured value suppliers and value consumers
 */
public class ConfiguredInstruction {
    public static final MapCodec<ConfiguredInstruction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    MinionRegistries.INSTRUCTION_TYPES.byNameCodec().fieldOf("instruction").forGetter(ConfiguredInstruction::getInstruction),
                    ValueSupplierList.CODEC.fieldOf("arguments").forGetter(ConfiguredInstruction::getArguments),
                    ValueConsumerList.CODEC.fieldOf("valueConsumers").forGetter(ConfiguredInstruction::getValueConsumers)
            ).apply(instance, ConfiguredInstruction::new)
    );

    public static final Codec<ConfiguredInstruction> CODEC = MAP_CODEC.codec();

    private final InstructionType instruction;
    private final ValueSupplierList arguments;
    private final ValueConsumerList valueConsumers;

    private List<Component> lastErrors = List.of();
    private ListenerManager<ConfiguredInstructionListener> listeners = new ListenerManager<>();

    private ConfiguredInstruction(
            InstructionType instruction,
            ValueSupplierList arguments,
            ValueConsumerList valueConsumers
    ) {
        this.instruction = instruction;
        this.arguments = arguments;
        this.valueConsumers = valueConsumers;
        arguments.addListener(this::onSupplierChange);
        valueConsumers.addListener(this::onConsumerChange);
    }

    public ConfiguredInstruction(InstructionType instruction) {
        this(instruction, new ValueSupplierList(), new ValueConsumerList());
    }

    public InstructionType getInstruction() {
        return instruction;
    }

    public ValueSupplierList getArguments() {
        return arguments;
    }

    public ValueConsumerList getValueConsumers() {
        return valueConsumers;
    }

    public List<Component> preCheck() {
        List<Component> errors = new ArrayList<>();
        arguments.checkRun(instruction, errors::add);
        return errors;
    }

    public boolean canRun() {
        return preCheck().isEmpty();
    }

    public List<Component> getLastErrors() {
        return lastErrors;
    }

    public OptionalInt run(InstructionRuntime runtime, Context resolutionContext) {
        OptionalInt id = OptionalInt.empty();
        if(canRun()) {
            lastErrors = new ArrayList<>();
            try {
                ParameterValueList resolvedArguments = arguments.resolve(resolutionContext, lastErrors::add);
                if(lastErrors.isEmpty()) {
                    id = OptionalInt.of(runtime.run(instruction, resolvedArguments));

                    for(ConfiguredInstructionListener listener : listeners) {
                        listener.onRun(this, runtime, id.getAsInt());
                    }
                }
            } catch (Exception e) {
                Minions.LOGGER.error("An error occurred while executing configured Instruction", e);
                lastErrors.add(Component.translatable("minions.gui.instruction.check.internal_error"));
            }
        }
        return id;
    }

    private void onSupplierChange(Parameter<?> parameter) {
        listeners.forEach(listener -> listener.onSupplierChange(this, parameter));
    }

    private void onConsumerChange(Parameter<?> parameter) {
        listeners.forEach(listener -> listener.onConsumerChange(this, parameter));
    }

    public void onInstructionRemove() {
        listeners.forEach(listener -> listener.onInstructionRemove(this));
    }

    public void addListener(ConfiguredInstructionListener listener) {
        listeners.addListener(listener);
    }

    public void removeListener(ConfiguredInstructionListener listener) {
        listeners.removeListener(listener);
    }
}

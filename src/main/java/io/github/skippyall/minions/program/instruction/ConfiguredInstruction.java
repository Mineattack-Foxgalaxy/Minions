package io.github.skippyall.minions.program.instruction;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.listener.SerializableListenerManager;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.supplier.Parameter;
import io.github.skippyall.minions.program.supplier.ParameterValueList;
import io.github.skippyall.minions.program.supplier.ValueSupplierList;
import io.github.skippyall.minions.registration.MinionRegistries;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

/**
 * Holds an instruction and its configuration
 * @param <R> The runtime that this object is configured for
 */
public class ConfiguredInstruction<R extends InstructionRuntime<R>> {
    public static final MapCodec<ConfiguredInstruction<?>> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    MinionRegistries.INSTRUCTION_TYPES.byNameCodec().fieldOf("instruction").forGetter(ConfiguredInstruction::getInstruction),
                    ValueSupplierList.CODEC.fieldOf("arguments").forGetter(ConfiguredInstruction::getArguments),
                    //runtime.getValueConsumerListCodec().fieldOf("valueConsumers").forGetter(ConfiguredInstruction::getValueConsumers),
                    SerializableListenerManager.getCodec(MinionRegistries.INSTRUCTION_LISTENER_CODECS).fieldOf("listeners").forGetter(i -> i.listeners)
            ).apply(instance, ConfiguredInstruction::new)
    );

    private final InstructionType<R> instruction;
    private final ValueSupplierList arguments;
    //private final ValueConsumerList<R> valueConsumers;

    private List<Component> lastErrors;
    private SerializableListenerManager<ConfiguredInstructionListener> listeners = new SerializableListenerManager<>();

    private ConfiguredInstruction(
            InstructionType<R> instruction,
            ValueSupplierList arguments,
            //ValueConsumerList<R> valueConsumers,
            SerializableListenerManager<ConfiguredInstructionListener> listeners
    ) {
        this(instruction, arguments /*, valueConsumers*/);
        this.listeners = listeners;
    }

    private ConfiguredInstruction(
            InstructionType<R> instruction,
            ValueSupplierList arguments //,
            //ValueConsumerList<R> valueConsumers
    ) {
        this.instruction = instruction;
        this.arguments = arguments;
        //this.valueConsumers = valueConsumers;
        arguments.addListener(this::onSupplierChange);
        //valueConsumers.addListener(this::onConsumerChange);
    }

    public ConfiguredInstruction(InstructionType<R> instruction) {
        this(instruction, new ValueSupplierList() /*, new ValueConsumerList<>(),*/);
    }

    public InstructionType<R> getInstruction() {
        return instruction;
    }

    public ValueSupplierList getArguments() {
        return arguments;
    }

    /*public ValueConsumerList<R> getValueConsumers() {
        return valueConsumers;
    }*/

    public List<Component> preCheck() {
        List<Component> errors = new ArrayList<>();
        arguments.checkRun(instruction, errors::add);
        return errors;
    }

    public boolean canRun() {
        return preCheck().isEmpty();
    }

    public OptionalInt run(R runtime) {
        OptionalInt id = OptionalInt.empty();
        if(canRun()) {
            lastErrors = new ArrayList<>();
            try {
                ParameterValueList resolvedArguments = arguments.resolve(runtime.getServer(), lastErrors::add);
                if(lastErrors.isEmpty()) {
                    InstructionExecution<R> execution = instruction.createExecution(resolvedArguments, runtime);
                    id = OptionalInt.of(runtime.addInstruction(new ExecutingInstruction<>(instruction, execution)));
                    execution.start(runtime);

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

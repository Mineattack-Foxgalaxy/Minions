package io.github.skippyall.minions.program.instruction;

import io.github.skippyall.minions.MinionRegistries;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.supplier.Parameter;
import io.github.skippyall.minions.program.supplier.ValueSupplierList;
import io.github.skippyall.minions.program.consumer.ValueConsumerList;
import io.github.skippyall.minions.util.SerializableListenerManager;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.jetbrains.annotations.Nullable;

public class ConfiguredInstruction<R extends InstructionRuntime<R>> {
    private final InstructionType<R> instruction;
    private final ValueSupplierList<R> arguments;
    private final ValueConsumerList<R> valueConsumers;
    private @Nullable InstructionExecution<R> execution;

    private SerializableListenerManager<ConfiguredInstructionListener> listeners = new SerializableListenerManager<>(MinionRegistries.INSTRUCTION_LISTENER_CODECS);

    private ConfiguredInstruction(InstructionType<R> instruction, ValueSupplierList<R> arguments, ValueConsumerList<R> valueConsumers, @Nullable InstructionExecution<R> execution, SerializableListenerManager<ConfiguredInstructionListener> listeners) {
        this(instruction, arguments, valueConsumers, execution);
        this.listeners = listeners;
    }

    private ConfiguredInstruction(InstructionType<R> instruction, ValueSupplierList<R> arguments, ValueConsumerList<R> valueConsumers, @Nullable InstructionExecution<R> execution) {
        this.instruction = instruction;
        this.arguments = arguments;
        this.valueConsumers = valueConsumers;
        this.execution = execution;
        arguments.addListener(this::onSupplierChange);
        valueConsumers.addListener(this::onConsumerChange);
    }

    public ConfiguredInstruction(InstructionType<R> instruction) {
        this(instruction, new ValueSupplierList<>(), new ValueConsumerList<>(), null);
    }

    public InstructionType<R> getInstruction() {
        return instruction;
    }

    public ValueSupplierList<R> getArguments() {
        return arguments;
    }

    public boolean canRun() {
        return instruction != null && arguments != null && arguments.hasArgumentForAll(instruction.getParameters());
    }

    public boolean isRunning() {
        return execution != null;
    }

    public @Nullable InstructionExecution<R> getExecution() {
        return execution;
    }

    public void run(R minion) {
        if(canRun() && !isRunning()) {
            try {
                execution = instruction.createExecution(arguments, minion);
                execution.start(minion);
            } catch (Exception e) {
                Minions.LOGGER.error("An error occurred while executing configured Instruction", e);
            }

            listeners.forEachListener(listener -> listener.onRun(this));
        }
    }

    public void tick(R minion) {
        if(execution != null) {
            if(execution.isDone(minion)) {
                stop(minion);
            } else {
                execution.tick(minion);
                if (execution.isDone(minion)) {
                    stop(minion);
                }
            }
        }
    }

    public void stop(R minion) {
        if(execution != null) {
            execution.stop(minion, valueConsumers);
            execution = null;
            listeners.forEachListener(listener -> listener.onStop(this));
        }
    }

    private void onSupplierChange(Parameter<?> parameter) {
        listeners.forEachListener(listener -> listener.onSupplierChange(this, parameter));
    }

    private void onConsumerChange(Parameter<?> parameter) {
        listeners.forEachListener(listener -> listener.onConsumerChange(this, parameter));
    }

    public void onInstructionRemove() {
        listeners.forEachListener(listener -> listener.onInstructionRemove(this));
    }

    public void addListener(ConfiguredInstructionListener listener) {
        listeners.addListener(listener);
    }

    public void removeListener(ConfiguredInstructionListener listener) {
        listeners.removeListener(listener);
    }

    public void save(WriteView view, R minion) {
        view.put("instruction", minion.getInstructionTypeRegistry().getCodec(), instruction);
        view.put("arguments", minion.getArgumentListCodec(), arguments);
        view.put("valueConsumers", minion.getValueConsumerListCodec(), valueConsumers);
        view.putBoolean("running", isRunning());
        if(execution != null) {
            execution.save(view.get("execution"), minion);
        }

        listeners.save(view);
    }

    public static <R extends InstructionRuntime<R>> ConfiguredInstruction<R> load(ReadView view, R minion) {
        InstructionType<R> instructionType = view.read("instruction", minion.getInstructionTypeRegistry().getCodec()).orElseThrow();

        ValueSupplierList<R> arguments = view.read("arguments", minion.getArgumentListCodec()).orElseGet(ValueSupplierList::new);
        ValueConsumerList<R> valueConsumers = view.read("valueConsumers", minion.getValueConsumerListCodec()).orElseGet(ValueConsumerList::new);

        boolean running = view.getBoolean("running", false);

        SerializableListenerManager<ConfiguredInstructionListener> listeners = new SerializableListenerManager<>(MinionRegistries.INSTRUCTION_LISTENER_CODECS);
        listeners.load(view);

        if(running) {
            ReadView executionView = view.getReadView("execution");
            try {
                InstructionExecution<R> execution = instructionType.loadExecution(executionView, minion);
                return new ConfiguredInstruction<>(instructionType, arguments, valueConsumers, execution, listeners);
            } catch (Exception e) {
                Minions.LOGGER.error("Error while loading execution", e);
            }
        }

        return new ConfiguredInstruction<>(instructionType, arguments, valueConsumers, null, listeners);
    }
}

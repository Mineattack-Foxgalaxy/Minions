package io.github.skippyall.minions.program.instruction;

import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.listener.SerializableListenerManager;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.consumer.ValueConsumerList;
import io.github.skippyall.minions.program.supplier.Parameter;
import io.github.skippyall.minions.program.supplier.ParameterValueList;
import io.github.skippyall.minions.program.supplier.ValueSupplierList;
import io.github.skippyall.minions.registration.MinionRegistries;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

/**
 * Holds an instruction, its configuration and is responsible for executing the instruction
 * @param <R> The runtime holding this object
 */
public class ConfiguredInstruction<R extends InstructionRuntime<R>> {
    private final InstructionType<R> instruction;
    private final ValueSupplierList<R> arguments;
    private final ValueConsumerList<R> valueConsumers;
    private @Nullable InstructionExecution<R> execution;
    private boolean paused = false;

    private SerializableListenerManager<ConfiguredInstructionListener> listeners = new SerializableListenerManager<>();

    private ConfiguredInstruction(
            InstructionType<R> instruction,
            ValueSupplierList<R> arguments,
            ValueConsumerList<R> valueConsumers,
            @Nullable InstructionExecution<R> execution,
            SerializableListenerManager<ConfiguredInstructionListener> listeners,
            boolean paused
    ) {
        this(instruction, arguments, valueConsumers, execution);
        this.listeners = listeners;
        this.paused = paused;
    }

    private ConfiguredInstruction(
            InstructionType<R> instruction,
            ValueSupplierList<R> arguments,
            ValueConsumerList<R> valueConsumers,
            @Nullable InstructionExecution<R> execution
    ) {
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
        return instruction != null && arguments != null && arguments.checkRun(instruction) == null;
    }

    public boolean isRunning() {
        return execution != null;
    }

    public @Nullable InstructionExecution<R> getExecution() {
        return execution;
    }

    public void run(R minion) {
        if(canRun() && !isRunning()) {
            ParameterValueList resolvedArguments = arguments.resolve(minion);
            try {
                execution = instruction.createExecution(resolvedArguments, minion);
                execution.start(minion);
            } catch (Exception e) {
                Minions.LOGGER.error("An error occurred while executing configured Instruction", e);
            }

            listeners.forEach(listener -> listener.onRun(this));
        }
    }

    public void tick(R minion) {
        if(execution != null && !paused) {
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
            listeners.forEach(listener -> listener.onStop(this));
        }
    }

    public void updatePauseStatus(R runtime) {
        boolean typeEnabled = runtime.isInstructionEnabled(instruction);
        if(typeEnabled && paused) {
            paused = false;
            if(execution != null) {
                execution.resume(runtime);
            }
        } else if(!typeEnabled && !paused) {
            paused = true;
            if(execution != null) {
                execution.pause(runtime);
            }
        }
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

    public void save(ValueOutput view, R runtime) {
        view.store("instruction", runtime.getInstructionTypeRegistry().byNameCodec(), instruction);
        view.store("arguments", runtime.getArgumentListCodec(), arguments);
        view.store("valueConsumers", runtime.getValueConsumerListCodec(), valueConsumers);
        view.putBoolean("running", isRunning());
        view.putBoolean("paused", paused);
        view.store("listeners", SerializableListenerManager.getCodec(MinionRegistries.INSTRUCTION_LISTENER_CODECS), listeners);

        if(execution != null) {
            execution.save(view.child("execution"), runtime);
        }
    }

    public static <R extends InstructionRuntime<R>> ConfiguredInstruction<R> load(ValueInput view, R minion) {
        InstructionType<R> instructionType = view.read("instruction", minion.getInstructionTypeRegistry().byNameCodec()).orElseThrow();

        ValueSupplierList<R> arguments = view.read("arguments", minion.getArgumentListCodec()).orElseGet(ValueSupplierList::new);
        ValueConsumerList<R> valueConsumers = view.read("valueConsumers", minion.getValueConsumerListCodec()).orElseGet(ValueConsumerList::new);

        boolean running = view.getBooleanOr("running", false);
        boolean paused = view.getBooleanOr("paused", false);

        SerializableListenerManager<ConfiguredInstructionListener> listeners = view.read(
                "listeners",
                SerializableListenerManager.getCodec(MinionRegistries.INSTRUCTION_LISTENER_CODECS)
        ).orElseGet(SerializableListenerManager::new);

        if(running) {
            ValueInput executionView = view.childOrEmpty("execution");
            try {
                InstructionExecution<R> execution = instructionType.loadExecution(executionView, minion);
                return new ConfiguredInstruction<>(instructionType, arguments, valueConsumers, execution, listeners, paused);
            } catch (Exception e) {
                Minions.LOGGER.error("Error while loading execution", e);
            }
        }

        return new ConfiguredInstruction<>(instructionType, arguments, valueConsumers, null, listeners, paused);
    }
}

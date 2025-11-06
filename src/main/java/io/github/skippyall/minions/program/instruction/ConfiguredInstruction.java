package io.github.skippyall.minions.program.instruction;

import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.supplier.ValueSupplierList;
import io.github.skippyall.minions.program.consumer.ValueConsumerList;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.jetbrains.annotations.Nullable;

public class ConfiguredInstruction<R extends InstructionRuntime<R>> {
    private final InstructionType<R> instruction;
    private final ValueSupplierList<R> arguments;
    private final ValueConsumerList<R> valueConsumers;
    private @Nullable InstructionExecution<R> execution;
    private final String name;

    private ConfiguredInstruction(InstructionType<R> instruction, ValueSupplierList<R> arguments, ValueConsumerList<R> valueConsumers, @Nullable InstructionExecution<R> execution, String name) {
        this.instruction = instruction;
        this.arguments = arguments;
        this.valueConsumers = valueConsumers;
        this.execution = execution;
        this.name = name;
    }

    public ConfiguredInstruction(InstructionType<R> instruction, String name) {
        this(instruction, new ValueSupplierList<>(), new ValueConsumerList<>(), null, name);
    }

    public InstructionType<R> getInstruction() {
        return instruction;
    }

    public ValueSupplierList<R> getArguments() {
        return arguments;
    }

    public String getName() {
        return name;
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
                Minions.LOGGER.error("An error occurred while executing configured Instruction {}", name, e);

            }
        }
    }

    public void tick(R minion) {
        if(isRunning()) {
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
        if(isRunning()) {
            execution.stop(minion, valueConsumers);
            execution = null;
        }
    }

    public void save(WriteView view, R minion) {
        view.put("instruction", minion.getInstructionTypeRegistry().getCodec(), instruction);
        view.put("arguments", minion.getArgumentListCodec(), arguments);
        view.put("valueConsumers", minion.getValueConsumerListCodec(), valueConsumers);
        view.putBoolean("running", isRunning());
        if(isRunning()) {
            execution.save(view.get("execution"), minion);
        }
    }

    public static <R extends InstructionRuntime<R>> ConfiguredInstruction<R> load(ReadView view, R minion, String name) {
        InstructionType<R> instructionType = view.read("instruction", minion.getInstructionTypeRegistry().getCodec()).orElseThrow();

        ValueSupplierList<R> arguments = view.read("arguments", minion.getArgumentListCodec()).orElseGet(ValueSupplierList::new);
        ValueConsumerList<R> valueConsumers = view.read("valueConsumers", minion.getValueConsumerListCodec()).orElseGet(ValueConsumerList::new);

        boolean running = view.getBoolean("running", false);

        if(running) {
            ReadView executionView = view.getReadView("execution");
            try {
                InstructionExecution<R> execution = instructionType.loadExecution(executionView, minion);
                return new ConfiguredInstruction<>(instructionType, arguments, valueConsumers, execution, name);
            } catch (Exception e) {
                Minions.LOGGER.error("Error while loading execution", e);
            }
        }

        return new ConfiguredInstruction<>(instructionType, arguments, valueConsumers, null, name);
    }
}

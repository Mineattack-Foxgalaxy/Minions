package io.github.skippyall.minions.minion;

import io.github.skippyall.minions.MinionRegistries;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.argument.GenericArgumentType;
import net.minecraft.registry.Registry;

public class MinionRuntime implements InstructionRuntime<MinionRuntime> {

    @Override
    public Registry<GenericArgumentType<MinionRuntime>> getGenericArgumentTypeRegistry() {
        return MinionRegistries.GENERIC_ARGUMENT_TYPE_REGISTRY;
    }
}

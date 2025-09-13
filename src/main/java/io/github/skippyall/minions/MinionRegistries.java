package io.github.skippyall.minions;

import com.mojang.serialization.Lifecycle;
import io.github.skippyall.minions.minion.skin.SkinProvider;
import io.github.skippyall.minions.program.argument.GenericArgumentType;
import io.github.skippyall.minions.program.instruction.InstructionType;
import io.github.skippyall.minions.program.value.ValueType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public class MinionRegistries {
    public static final Registry<ValueType<?>> VALUE_TYPES = registry("value_type");
    public static final Registry<GenericArgumentType> GENERIC_ARGUMENT_TYPE_REGISTRY = registry("generic_argument_type");
    public static final Registry<InstructionType<?>> INSTRUCTION_TYPES = registry("instruction_type");
    public static final Registry<SkinProvider> SKIN_PROVIDERS = registry("skin_providers");

    private static <T> Registry<T> registry(String id) {
        return new SimpleRegistry<>(RegistryKey.ofRegistry(Identifier.of(Minions.MOD_ID, id)), Lifecycle.stable());
    }
}

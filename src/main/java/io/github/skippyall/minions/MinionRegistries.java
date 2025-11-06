package io.github.skippyall.minions;

import com.mojang.serialization.Lifecycle;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.skin.SkinProvider;
import io.github.skippyall.minions.program.supplier.ValueSupplierType;
import io.github.skippyall.minions.program.instruction.InstructionType;
import io.github.skippyall.minions.program.consumer.ValueConsumerType;
import io.github.skippyall.minions.program.value.ValueType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public class MinionRegistries {
    public static final Registry<ValueType<?>> VALUE_TYPES = registry("value_type");
    public static final Registry<ValueSupplierType<MinionRuntime>> ARGUMENT_TYPES = registry("argument_type");
    public static final Registry<ValueConsumerType<MinionRuntime>> VALUE_CONSUMER_TYPES = registry("value_consumer_type");
    public static final Registry<InstructionType<MinionRuntime>> INSTRUCTION_TYPES = registry("instruction_type");
    public static final Registry<SkinProvider> SKIN_PROVIDERS = registry("skin_providers");

    private static <T> Registry<T> registry(String id) {
        return new SimpleRegistry<>(RegistryKey.ofRegistry(Identifier.of(Minions.MOD_ID, id)), Lifecycle.stable());
    }
}

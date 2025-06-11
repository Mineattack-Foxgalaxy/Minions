package io.github.skippyall.minions.new_program.argument;

import com.mojang.serialization.Lifecycle;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.new_program.value.ValueType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public class Arguments {
    public static final Registry<GenericArgumentType> GENERIC_ARGUMENT_TYPE_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(Identifier.of("minions", "generic_argument_type")), Lifecycle.stable());

    public static final GenericArgumentType VALUE_ARGUMENT = register(Identifier.of(Minions.MOD_ID, "value"), ValueArgumentType::new);

    public static <V> SpecificArgumentType<V, ?> getArgumentType(Identifier id, ValueType<V> valueType) {
        GenericArgumentType generic = GENERIC_ARGUMENT_TYPE_REGISTRY.get(id);
        if(generic != null) {
            return generic.createTypeSpecific(valueType);
        }

        return null;
    }

    public static GenericArgumentType register(Identifier id, GenericArgumentType argumentType) {
        return Registry.register(GENERIC_ARGUMENT_TYPE_REGISTRY, id, argumentType);
    }
}

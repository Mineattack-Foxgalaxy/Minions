package io.github.skippyall.minions.new_program.argument;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import io.github.skippyall.minions.MinionRegistries;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.new_program.value.ValueType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public class Arguments {
    public static final Codec<SpecificArgumentType<?,?>> SPECIFIC_ARGUMENT_TYPE_CODEC = MinionRegistries.GENERIC_ARGUMENT_TYPE_REGISTRY.getCodec().dispatch(
            "type",
            SpecificArgumentType::getGenericArgumentType,
            generic ->
                    MinionRegistries.VALUE_TYPES.getCodec().xmap(
                            generic::createTypeSpecific,
                            SpecificArgumentType::getValueType
                    ).fieldOf("valueType")
    );

    public static final Codec<Argument<?,?>> ARGUMENT_CODEC = SPECIFIC_ARGUMENT_TYPE_CODEC.dispatch(Argument::getType, specific -> specific.getArgumentCodec().fieldOf("data"));

    public static final GenericArgumentType VALUE_ARGUMENT = register(Identifier.of(Minions.MOD_ID, "value"), ValueArgumentType::new);

    /*public static <V> SpecificArgumentType<V, ? extends Argument<V, ?>> getArgumentType(Identifier id, ValueType<V> valueType) {
        GenericArgumentType generic = MinionRegistries.GENERIC_ARGUMENT_TYPE_REGISTRY.get(id);
        if(generic != null) {
            return generic.createTypeSpecific(valueType);
        }

        return null;
    }*/

    public static GenericArgumentType register(Identifier id, GenericArgumentType argumentType) {
        return Registry.register(MinionRegistries.GENERIC_ARGUMENT_TYPE_REGISTRY, id, argumentType);
    }
}

package io.github.skippyall.minions.program.argument;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.MinionRegistries;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.value.ValueType;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class Arguments {
    public static final Codec<SpecificArgumentType<?,?, MinionFakePlayer>> SPECIFIC_ARGUMENT_TYPE_CODEC = createSpecificTypeCodec(MinionRegistries.GENERIC_ARGUMENT_TYPE_REGISTRY.getCodec());
    public static final Codec<Argument<?,?,MinionFakePlayer>> ARGUMENT_CODEC = createArgumentCodecFromSpecific(SPECIFIC_ARGUMENT_TYPE_CODEC);

    public static final GenericArgumentType<MinionFakePlayer> VALUE_ARGUMENT = register(Identifier.of(Minions.MOD_ID, "value"), ValueArgumentType::new);

    public static <R> Codec<SpecificArgumentType<?,?,R>> createSpecificTypeCodec(Codec<GenericArgumentType<R>> genericCodec) {
        return genericCodec.dispatch(
                "type",
                SpecificArgumentType::getGenericArgumentType,
                generic ->
                        MinionRegistries.VALUE_TYPES.getCodec().xmap(
                                generic::createTypeSpecific,
                                SpecificArgumentType::getValueType
                        ).fieldOf("valueType")
        );
    }

    public static <R extends InstructionRuntime<R>> Codec<Argument<?, ?, R>> createArgumentCodecFromSpecific(Codec<SpecificArgumentType<?,?,R>> specificTypeCodec) {
        return specificTypeCodec.dispatch(Argument::getType, specific -> specific.getArgumentCodec().fieldOf("data"));
    }

    public static <R extends InstructionRuntime<R>> Codec<Argument<?,?,R>> createArgumentCodec(Codec<GenericArgumentType<R>> genericTypeCodec) {
        return createArgumentCodecFromSpecific(createSpecificTypeCodec(genericTypeCodec));
    }

    public static <V> SpecificArgumentType<V, ? extends Argument<V, ?, MinionFakePlayer>, MinionFakePlayer> getArgumentType(Identifier id, ValueType<V> valueType) {
        GenericArgumentType<MinionFakePlayer> generic = MinionRegistries.GENERIC_ARGUMENT_TYPE_REGISTRY.get(id);
        if(generic != null) {
            return generic.createTypeSpecific(valueType);
        }

        return null;
    }

    public static GenericArgumentType<MinionFakePlayer> register(Identifier id, GenericArgumentType<MinionFakePlayer> argumentType) {
        return Registry.register(MinionRegistries.GENERIC_ARGUMENT_TYPE_REGISTRY, id, argumentType);
    }
}

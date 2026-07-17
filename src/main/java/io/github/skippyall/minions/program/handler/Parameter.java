package io.github.skippyall.minions.program.handler;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;

public record Parameter<T>(String name, ValueType<T> type) {
    public static final MapCodec<Parameter<?>> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("name").forGetter(Parameter::name),
                    MinionRegistries.VALUE_TYPES.byNameCodec().fieldOf("type").forGetter(Parameter::type)
            ).apply(instance, Parameter::new));
    public static final Codec<Parameter<?>> CODEC = MAP_CODEC.codec();
}
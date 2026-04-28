package io.github.skippyall.minions.registration;

import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.program.conversion.CastConverter;
import io.github.skippyall.minions.program.conversion.EqualityConverter;
import io.github.skippyall.minions.program.conversion.ValueConverterType;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ValueConverters {
    public static final EqualityConverter.EqualityConverterType EQUALITY_CONVERTER = register("equality", new EqualityConverter.EqualityConverterType());
    public static final CastConverter.Type CAST_CONVERTER = register("cast", new CastConverter.Type());

    private static <T extends ValueConverterType<?>> T register(String name, T type) {
        return Registry.register(MinionRegistries.VALUE_CONVERTER_TYPES, Identifier.of(Minions.MOD_ID, name), type);
    }

    public static void register() {}
}

package io.github.skippyall.minions.registration;

import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.program.conversion.ArithmeticConverter;
import io.github.skippyall.minions.program.conversion.CastConverter;
import io.github.skippyall.minions.program.conversion.DivideByConverter;
import io.github.skippyall.minions.program.conversion.DivideConverter;
import io.github.skippyall.minions.program.conversion.EqualityConverter;
import io.github.skippyall.minions.program.conversion.MapConverter;
import io.github.skippyall.minions.program.conversion.ValueConverterType;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class ValueConverters {
    public static final EqualityConverter.Type EQUALITY_CONVERTER = register("equality", new EqualityConverter.Type());
    public static final CastConverter.Type CAST_CONVERTER = register("cast", new CastConverter.Type());
    public static final MapConverter.Type MAP_CONVERTER = register("map", new MapConverter.Type());

    public static final ArithmeticConverter.Type ADD_CONVERTER = register("add", new ArithmeticConverter.Type(
            (in, with) -> in + with,
            (in, with) -> in + with
    ));
    public static final ArithmeticConverter.Type SUBTRACT_CONVERTER = register("subtract", new ArithmeticConverter.Type(
            (in, with) -> in - with,
            (in, with) -> in - with
    ));
    public static final ArithmeticConverter.Type SUBTRACT_FROM_CONVERTER = register("subtract_from", new ArithmeticConverter.Type(
            (in, with) -> with - in,
            (in, with) -> with - in
    ));
    public static final ArithmeticConverter.Type MULTIPLY_CONVERTER = register("multiply", new ArithmeticConverter.Type(
            (in, with) -> in * with,
            (in, with) -> in * with
    ));
    public static final DivideByConverter.Type DIVIDE_BY_CONVERTER = register("divide_by", new DivideByConverter.Type());
    public static final DivideConverter.Type DIVIDE_CONVERTER = register("divide", new DivideConverter.Type());

    private static <T extends ValueConverterType<?>> T register(String name, T type) {
        return Registry.register(MinionRegistries.VALUE_CONVERTER_TYPES, Identifier.fromNamespaceAndPath(Minions.MOD_ID, name), type);
    }

    public static void register() {}

    private static @Nullable Component checkNotZero(double v) {
        if(v == 0) {
            return Component.translatable("value_converter.minions.divide.divide_by_zero");
        } else {
            return null;
        }
    }
}

package io.github.skippyall.minions.registration;

import io.github.skippyall.minions.minion.program.supplier.AnalogInputSupplier;
import io.github.skippyall.minions.program.supplier.FixedValueSupplierType;
import io.github.skippyall.minions.program.supplier.ValueSupplierType;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.minion.MinionRuntime;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ValueSuppliers {
    public static final FixedValueSupplierType<MinionRuntime> FIXED_VALUE_SUPPLIER_TYPE = register("fixed", new FixedValueSupplierType<>());
    public static final AnalogInputSupplier.AnalogInputSupplierType ANALOG_INPUT = register("analog_input", new AnalogInputSupplier.AnalogInputSupplierType());

    public static <T extends ValueSupplierType<MinionRuntime>> T register(String id, T type) {
        return Registry.register(MinionRegistries.VALUE_SUPPLIER_TYPES, Identifier.of(Minions.MOD_ID, id), type);
    }

    public static void register() {}
}

package io.github.skippyall.minions.program.supplier;

import io.github.skippyall.minions.MinionRegistries;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.minion.MinionRuntime;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ValueSuppliers {
    public static final FixedValueSupplierType<MinionRuntime> FIXED_VALUE_SUPPLIER_TYPE = register("fixed", new FixedValueSupplierType<>());

    public static <T extends ValueSupplierType<MinionRuntime>> T register(String id, T type) {
        return Registry.register(MinionRegistries.VALUE_SUPPLIER_TYPES, Identifier.of(Minions.MOD_ID, id), type);
    }

    public static void register() {}
}

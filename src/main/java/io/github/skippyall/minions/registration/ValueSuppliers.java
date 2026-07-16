package io.github.skippyall.minions.registration;

import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.block.miniontrigger.ConnectedBlockSupplier;
import io.github.skippyall.minions.program.supplier.FixedValueSupplierType;
import io.github.skippyall.minions.program.supplier.ValueSupplierType;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class ValueSuppliers {
    public static final FixedValueSupplierType FIXED_VALUE_SUPPLIER_TYPE = register("fixed", new FixedValueSupplierType());
    public static final ConnectedBlockSupplier.ConnectedBlockSupplierType CONNECTED_BLOCK_SUPPLIER_TYPE = register("connected_block", new ConnectedBlockSupplier.ConnectedBlockSupplierType());
    //public static final AnalogInputSupplier.AnalogInputSupplierType ANALOG_INPUT = register("analog_input", new AnalogInputSupplier.AnalogInputSupplierType());

    public static <T extends ValueSupplierType> T register(String id, T type) {
        return Registry.register(MinionRegistries.VALUE_SUPPLIER_TYPES, Identifier.fromNamespaceAndPath(Minions.MOD_ID, id), type);
    }

    public static void register() {}
}

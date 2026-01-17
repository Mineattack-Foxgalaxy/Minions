package io.github.skippyall.minions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.skippyall.minions.docs.ReferenceEntry;
import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.minion.MinionListener;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.skin.SkinProvider;
import io.github.skippyall.minions.module.SpecialAbility;
import io.github.skippyall.minions.program.instruction.ConfiguredInstructionListener;
import io.github.skippyall.minions.program.supplier.ValueSupplierType;
import io.github.skippyall.minions.program.instruction.InstructionType;
import io.github.skippyall.minions.program.consumer.ValueConsumerType;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.reference.Reference;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class MinionRegistries {
    public static final Registry<ValueType<?>> VALUE_TYPES = registry("value_type");
    public static final Registry<ValueSupplierType<MinionRuntime>> VALUE_SUPPLIER_TYPES = registry("value_supplier_type");
    public static final Registry<ValueConsumerType<MinionRuntime>> VALUE_CONSUMER_TYPES = registry("value_consumer_type");
    public static final Registry<InstructionType<MinionRuntime>> INSTRUCTION_TYPES = registry("instruction_type");
    public static final Registry<SkinProvider> SKIN_PROVIDERS = registry("skin_provider");
    public static final Registry<Codec<? extends GuiDisplay>> GUI_DISPLAY_TYPE = registry("gui_display_type");
    public static final Registry<Codec<? extends ConfiguredInstructionListener>> INSTRUCTION_LISTENER_CODECS = registry("instruction_listener_codec");
    public static final Registry<Codec<? extends MinionListener>> MINION_LISTENER_CODECS = registry("minion_listener_codec");
    public static final Registry<MapCodec<? extends Reference>> REFERENCE_CODEC = registry("reference_codec");
    public static final Registry<SpecialAbility> SPECIAL_ABILITIES = registry("special_ability");

    public static final RegistryKey<Registry<GuiDisplay>> GUI_DISPLAY = key("gui_display");
    public static final RegistryKey<Registry<ReferenceEntry>> REFERENCE_ENTRY = key("reference_entry");

    private static <T> Registry<T> registry(String id) {
        return FabricRegistryBuilder.<T>createSimple(key(id)).attribute(RegistryAttribute.OPTIONAL).buildAndRegister();
    }

    private static <T> RegistryKey<Registry<T>> key(String name) {
        return RegistryKey.ofRegistry(Identifier.of(Minions.MOD_ID, name));
    }

    public static void register() {
        DynamicRegistries.register(GUI_DISPLAY, GuiDisplay.CODEC);
        DynamicRegistries.register(REFERENCE_ENTRY, ReferenceEntry.CODEC);
    }
}

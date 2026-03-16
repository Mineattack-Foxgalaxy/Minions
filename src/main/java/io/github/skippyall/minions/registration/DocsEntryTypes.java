package io.github.skippyall.minions.registration;

import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.docs.ReferenceEntry;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class DocsEntryTypes {
    public static void register() {
        Registry.register(MinionRegistries.DOCS_ENTRY_TYPES, Identifier.of(Minions.MOD_ID, "reference_entry"), ReferenceEntry.CODEC);
    }
}

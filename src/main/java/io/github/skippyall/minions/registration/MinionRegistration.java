package io.github.skippyall.minions.registration;

public class MinionRegistration {
    public static void register() {
        MinionRegistries.register();

        ClipboardTypes.register();
        DocsEntryTypes.register();
        GuiDisplayTypes.register();
        Instructions.register();
        MinionBlocks.register();
        MinionComponentTypes.register();
        MinionConfigOptions.register();
        MinionItems.register();
        SkinProviders.register();
        SpecialAbilities.register();
        ValueConverters.register();
        ValueSuppliers.register();
        ValueTypes.register();

        MinionCreativeTab.registerGroup();
    }
}

package io.github.skippyall.minions.registration;

public class MinionRegistration {
    public static void register() {
        MinionRegistries.register();

        ClipboardTypes.register();
        GuiDisplayTypes.register();
        Instructions.register();
        MinionBlocks.register();
        MinionComponentTypes.register();
        MinionItems.register();
        MinionListeners.register();
        SkinProviders.register();
        SpecialAbilities.register();
        ValueSuppliers.register();
        ValueTypes.register();

        MinionCreativeTab.registerGroup();
    }
}

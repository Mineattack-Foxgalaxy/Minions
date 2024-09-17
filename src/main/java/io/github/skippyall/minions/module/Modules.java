package io.github.skippyall.minions.module;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class Modules {
    public static void register() {
        ChatModule.registerMe();
        MountModule.registerMe();
        MoveModule.registerMe();
        MobSpawningModule.registerMe();
    }

    public static <T extends Item & ModuleItem> T register(Identifier id, T item) {
        return Registry.register(Registries.ITEM, id, item);
    }
}

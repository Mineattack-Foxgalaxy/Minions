package io.github.skippyall.minions.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public class ModelIdUtil {
    public static Identifier getItemModelId(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }
}

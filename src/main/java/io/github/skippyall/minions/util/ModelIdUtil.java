package io.github.skippyall.minions.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ModelIdUtil {
    public static ResourceLocation getItemModelId(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }
}

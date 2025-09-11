package io.github.skippyall.minions.util;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ModelIdUtil {
    public static Identifier getItemModelId(Item item) {
        Identifier identifier = Registries.ITEM.getId(item);
        return identifier.withPrefixedPath("item/");
    }
}

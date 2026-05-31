package io.github.skippyall.minions.polymer.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import io.github.skippyall.minions.polymer.VersionSync;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class SimpleItemOverlay implements PolymerItem {
    private final Item serverItem;
    private final Item polymerItem;
    private final boolean useModel;

    private SimpleItemOverlay(Item serverItem, Item polymerItem, boolean useModel) {
        this.serverItem = serverItem;
        this.polymerItem = polymerItem;
        this.useModel = useModel;
    }

    public static SimpleItemOverlay withModel(Item serverItem, Item polymerItem) {
        return new SimpleItemOverlay(serverItem, polymerItem, true);
    }

    public static SimpleItemOverlay withoutModel(Item serverItem, Item polymerItem) {
        return new SimpleItemOverlay(serverItem, polymerItem, false);
    }

    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        if(useModel && VersionSync.isOnClient(context)) {
            return serverItem;
        } else {
            return polymerItem;
        }
    }

    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context, HolderLookup.Provider lookup) {
        if(PolymerResourcePackUtils.hasMainPack(context) && useModel) {
            return PolymerItem.super.getPolymerItemModel(stack, context, lookup);
        } else {
            return null;
        }
    }
}

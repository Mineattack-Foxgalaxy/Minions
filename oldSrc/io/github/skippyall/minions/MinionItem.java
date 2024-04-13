package io.github.skippyall.minions;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.Nullable;

public class MinionItem extends Item implements PolymerItem {
    public MinionItem() {
        super(new FabricItemSettings());
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayer player) {
        return Items.PLAYER_HEAD;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack stack, TooltipFlag flag, ServerPlayer player) {
        ItemStack out = PolymerItemUtils.createItemStack(stack, flag, player);
        //CompoundTag tag = out.getOrCreateTag();
        //PlayerHeadItem.TAG_SKULL_OWNER;
        return out;
    }

    /*@Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayer player) {
        return 10;
    }*/

    public InteractionResult useOn(UseOnContext context) {
        System.out.println("Minion spawned "+ context.getItemInHand().getDisplayName());
        ComponentContents contents = context.getItemInHand().getHoverName().getContents();
        String name;
        if(contents instanceof PlainTextContents plainContents) {
            name = plainContents.text();
        } else {
            name = "Minion";
        }
        if(!context.getLevel().isClientSide) {
            MinionFakePlayer.createMinion(name, (ServerLevel) context.getLevel(), (ServerPlayer) context.getPlayer(), context.getClickedPos().getCenter().add(0,0.5,0), 0, 0);
        }
        return InteractionResult.SUCCESS;
    }
}

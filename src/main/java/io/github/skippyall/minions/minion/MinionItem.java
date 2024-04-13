package io.github.skippyall.minions.minion;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.TextContent;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;

public class MinionItem extends Item implements PolymerItem {
    private boolean canProgram;
    public MinionItem(boolean canProgram) {
        super(new FabricItemSettings());
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.ARMOR_STAND;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack stack, TooltipContext flag, ServerPlayerEntity player) {
        ItemStack out = PolymerItemUtils.createItemStack(stack, flag, player);
        out.addEnchantment(Enchantments.MENDING, 1);
        return out;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        String contents = context.getStack().getName().getLiteralString();
        String name;
        if(contents != null) {
            name = contents;
        } else {
            name = "Minion";
        }
        if(!context.getWorld().isClient) {
            MinionFakePlayer.createMinion(name, (ServerWorld) context.getWorld(), (ServerPlayerEntity) context.getPlayer(), canProgram, context.getBlockPos().toCenterPos().add(0,0.5,0), 0, 0);
        }
        return ActionResult.SUCCESS;
    }
}

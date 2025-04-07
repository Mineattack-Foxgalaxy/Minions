package io.github.skippyall.minions.minion;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public class MinionItem extends Item implements PolymerItem {
    public MinionItem(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context) {
        return null;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext player) {
        return Items.ARMOR_STAND;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack stack, TooltipType tooltipType, PacketContext player) {
        ItemStack out = PolymerItemUtils.createItemStack(stack, tooltipType, player);
        out.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        return out;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        MinionData data = getData(stack);
        if(data != null) {
            tooltip.add(Text.translatable("minions.minion_item.tooltip", data.name()));
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if(!context.getWorld().isClient) {
            MinionData data = getDataOrDefault(context.getStack());
            MinionFakePlayer.spawnMinion(data, (ServerWorld) context.getWorld(), context.getBlockPos().toCenterPos().add(0,0.5,0), new Vec2f(0, 0));
        }
        context.getStack().decrement(1);
        return ActionResult.SUCCESS;
    }

    public static void setData(MinionData data, ItemStack item) {
        item.set(MinionData.COMPONENT, data.uuid());
    }

    @Nullable
    public static MinionData getData(ItemStack item) {
        if(item.contains(MinionData.COMPONENT)) {
            return MinionPersistentState.INSTANCE.getMinionData(item.get(MinionData.COMPONENT));
        }
        return null;
    }

    public static MinionData getDataOrDefault(ItemStack item) {
        MinionData data = getData(item);
        if(data == null) {
            data = MinionData.createDefault();
        }
        return data;
    }

    public static boolean containsData(ItemStack item) {
        return item.contains(MinionData.COMPONENT);
    }
}

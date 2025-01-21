package io.github.skippyall.minions.minion;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;
import java.util.Optional;

public class MinionItem extends Item implements PolymerItem {
    private final boolean canProgram;

    public MinionItem(Settings settings, boolean canProgram) {
        super(settings);
        this.canProgram = canProgram;
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
        MinionData data = stack.get(MinionData.COMPONENT);
        if(data != null) {
            tooltip.add(Text.translatable("minions.minion_item.tooltip", data.name()));
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        String contents = context.getStack().getName().getLiteralString();
        String name;
        if(contents != null && contents.length() <= 16) {
            name = contents;
        } else {
            name = "Minion";
        }
        if(!context.getWorld().isClient) {
            MinionData data = getData(context.getStack());

            if(data == null) {
                data = new MinionData(null, name, Optional.empty());
            }

            if (data.uuid() == null) {
                MinionFakePlayer.createMinion(data, (ServerWorld) context.getWorld(), (ServerPlayerEntity) context.getPlayer(), canProgram, context.getBlockPos().toCenterPos().add(0,0.5,0), new Vec2f(0, 0));
            }else {
                data = data.withName(name);
                MinionFakePlayer.spawnMinionAt(data, (ServerWorld) context.getWorld(), context.getBlockPos().toCenterPos().add(0,0.5,0), new Vec2f(0, 0));
                MinionPersistentState.INSTANCE.addMinion(data);
            }
        }
        context.getStack().decrement(1);
        return ActionResult.SUCCESS;
    }

    public static void setData(MinionData data, ItemStack item) {
        item.set(MinionData.COMPONENT, data);
    }

    @Nullable
    public static MinionData getData(ItemStack item) {
        return item.get(MinionData.COMPONENT);
    }

    public static boolean containsData(ItemStack item) {
        NbtComponent nbt = item.get(DataComponentTypes.CUSTOM_DATA);
        if (nbt == null) {
            return false;
        }
        return nbt.copyNbt().contains("data");
    }
}

package io.github.skippyall.minions.minion;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.Nullable;

public class MinionItem extends Item implements PolymerItem {
    private final boolean canProgram;

    public MinionItem(boolean canProgram) {
        super(new Item.Settings());
        this.canProgram = canProgram;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.ARMOR_STAND;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack stack, TooltipType tooltipType, RegistryWrapper.WrapperLookup lookup, ServerPlayerEntity player) {
        ItemStack out = PolymerItemUtils.createItemStack(stack, lookup, player);
        out.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        return out;
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
                data = new MinionData(null, name, null);
            }

            if (data.uuid == null) {
                MinionFakePlayer.createMinion(data, (ServerWorld) context.getWorld(), (ServerPlayerEntity) context.getPlayer(), canProgram, context.getBlockPos().toCenterPos().add(0,0.5,0), 0, 0);
            }else {
                data.name = name;
                MinionFakePlayer.spawnMinionAt(data, (ServerWorld) context.getWorld(), context.getBlockPos().toCenterPos().add(0,0.5,0), new Vec2f(0, 0));
                MinionPersistentState.INSTANCE.addMinion(data);
            }
        }
        context.getStack().decrement(1);
        return ActionResult.SUCCESS;
    }

    public static void setData(MinionData data, ItemStack item) {
        NbtCompound nbt = item.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        nbt.put("data", data.writeNbt());
        item.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    @Nullable
    public static MinionData getData(ItemStack item) {
        NbtCompound nbt = item.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        if (nbt.getType("data") == NbtElement.COMPOUND_TYPE) {
            return MinionData.readNbt(nbt.getCompound("data"));
        }
        return null;
    }

    public static boolean containsData(ItemStack item) {
        NbtComponent nbt = item.get(DataComponentTypes.CUSTOM_DATA);
        if (nbt == null) {
            return false;
        }
        return nbt.copyNbt().contains("data");
    }
}

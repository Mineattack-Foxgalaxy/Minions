package io.github.skippyall.minions.minion;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import io.github.skippyall.minions.gui.MinionLookGui;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.function.Consumer;

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
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent component, Consumer<Text> tooltip, TooltipType type) {
        MinionData data = null /*getData(stack)*/;
        if(data != null) {
            tooltip.accept(Text.translatable("minions.minion_item.tooltip", data.name()));
        }
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if(user instanceof ServerPlayerEntity serverPlayer) {
            ItemStack stack = user.getStackInHand(hand);
            MinionLookGui.open(serverPlayer, stack);
            return ActionResult.SUCCESS;
        }

        return ActionResult.SUCCESS_SERVER;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if(!context.getWorld().isClient) {
            MinionData data = getDataOrDefault(context.getWorld().getServer(), context.getStack());
            MinionFakePlayer.spawnMinion(data, (ServerWorld) context.getWorld(), context.getBlockPos().toCenterPos().add(0,0.5,0), new Vec2f(0, 0));
        }
        context.getStack().decrement(1);
        return ActionResult.SUCCESS;
    }

    public static void setData(MinecraftServer server, MinionData data, ItemStack item) {
        item.set(MinionData.COMPONENT, data.uuid());
        MinionPersistentState.get(server).updateMinionData(data);
    }

    @Nullable
    public static MinionData getData(MinecraftServer server, ItemStack item) {
        if(item.contains(MinionData.COMPONENT)) {
            return MinionPersistentState.get(server).getMinionData(item.get(MinionData.COMPONENT));
        }
        return null;
    }

    public static MinionData getDataOrDefault(MinecraftServer server, ItemStack item) {
        MinionData data = getData(server, item);
        if(data == null) {
            data = MinionData.createDefault(server);
            setData(server, data, item);
        }
        return data;
    }

    public static boolean containsData(ItemStack item) {
        return item.contains(MinionData.COMPONENT);
    }
}

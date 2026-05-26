package io.github.skippyall.minions.minion;

import eu.pb4.polymer.core.api.item.PolymerItem;
import io.github.skippyall.minions.gui.MinionLookGui;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.registration.MinionComponentTypes;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import org.jspecify.annotations.Nullable;

public class MinionItem extends Item implements PolymerItem {
    public MinionItem(Properties settings) {
        super(settings);
    }

    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context, HolderLookup.Provider lookup) {
        return null;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext player) {
        return Items.ARMOR_STAND;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack stack, TooltipFlag tooltipType, PacketContext player, HolderLookup.Provider lookup) {
        ItemStack out = PolymerItem.super.getPolymerItemStack(stack, tooltipType, player, lookup);
        out.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        return out;
    }

    /*@Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay component, Consumer<Component> tooltip, TooltipFlag type) {
        //MinionData data = getData(stack);
        //if(data != null) {
        //    tooltip.accept(Component.translatable("minions.minion_item.tooltip", data.name()));
        //}
    }*/

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        if(user instanceof ServerPlayer serverPlayer) {
            ItemStack stack = user.getItemInHand(hand);
            new MinionLookGui(serverPlayer, stack);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(!context.getLevel().isClientSide()) {
            MinionData data = getDataOrDefault(context.getLevel().getServer(), context.getItemInHand());
            MinionFakePlayer.spawnMinion(data, (ServerLevel) context.getLevel(), context.getClickedPos().getCenter().add(0,0.5,0), new Vec2(0, 0));
        }
        context.getItemInHand().shrink(1);
        return InteractionResult.SUCCESS;
    }

    public static void setData(MinecraftServer server, MinionData data, ItemStack item) {
        item.set(MinionComponentTypes.MINION_DATA, data.getUuid());
        MinionPersistentState.get(server).updateMinionData(data);
    }

    @Nullable
    public static MinionData getData(MinecraftServer server, ItemStack item) {
        if(item.has(MinionComponentTypes.MINION_DATA)) {
            return MinionPersistentState.get(server).getMinionData(item.get(MinionComponentTypes.MINION_DATA));
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
        return item.has(MinionComponentTypes.MINION_DATA);
    }
}

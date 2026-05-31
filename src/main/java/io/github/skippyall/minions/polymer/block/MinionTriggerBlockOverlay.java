package io.github.skippyall.minions.polymer.block;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.block.miniontrigger.MinionTriggerBlock;
import io.github.skippyall.minions.polymer.VersionSync;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class MinionTriggerBlockOverlay implements PolymerBlock, PolymerClientDecoded, BlockWithElementHolder {
    @Override
    public BlockState getPolymerBlockState(BlockState state, @Nullable PacketContext context) {
        return VersionSync.isOnClient(context) ? state : net.minecraft.world.level.block.Blocks.COMPARATOR.defaultBlockState().setValue(DiodeBlock.POWERED, state.getValue(MinionTriggerBlock.POWERED));
    }

    @Override
    public boolean handleMiningOnServer(ItemStack tool, BlockState state, BlockPos pos, ServerPlayer player) {
        return false;
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        ElementHolder holder = new ElementHolder() {
            @Override
            public boolean startWatching(ServerGamePacketListenerImpl player) {
                if(PolymerResourcePackUtils.hasMainPack(player) && !VersionSync.isOnClient(player)) {
                    return super.startWatching(player);
                } else {
                    return false;
                }
            }
        };
        ItemStack stack = new ItemStack(Items.BARRIER);
        stack.set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(Minions.MOD_ID, "minion_trigger_no_plate_" + (initialBlockState.getValue(MinionTriggerBlock.POWERED) ? "active" : "inactive")));

        ItemDisplayElement element = new ItemDisplayElement(stack);
        element.setItemDisplayContext(ItemDisplayContext.NONE);
        holder.addElement(element);
        return holder;
    }
}

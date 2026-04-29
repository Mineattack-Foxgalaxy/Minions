package io.github.skippyall.minions.block.miniontrigger;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.core.api.utils.PolymerKeepModel;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.block.instruction_bound.InstructionBoundBlock;
import io.github.skippyall.minions.polymer.VersionSync;
import io.github.skippyall.minions.registration.MinionBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public class MinionTriggerBlock extends InstructionBoundBlock implements PolymerBlock, PolymerKeepModel, PolymerClientDecoded, BlockWithElementHolder {
    public static final MapCodec<MinionTriggerBlock> CODEC = simpleCodec(MinionTriggerBlock::new);

    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final VoxelShape SHAPE = Block.column(16.0, 0.0, 2.0);

    public MinionTriggerBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos blockPos = pos.below();
        return this.canPlaceAbove(world, blockPos, world.getBlockState(blockPos));
    }

    protected boolean canPlaceAbove(LevelReader world, BlockPos pos, BlockState state) {
        return state.isFaceSturdy(world, pos, Direction.UP, SupportType.RIGID);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, @Nullable Orientation wireOrientation, boolean notify) {
        if(!canSurvive(state, world, pos)) {
            dropResources(state, world, pos);
            world.removeBlock(pos, false);
            return;
        }

        boolean newPower = world.hasNeighborSignal(pos);
        if(state.getValue(POWERED) != newPower) {
            world.setBlockAndUpdate(pos, state.setValue(POWERED, newPower));
            world.getBlockEntity(pos, MinionBlocks.MINION_TRIGGER_BE_TYPE).ifPresent(MinionTriggerBlockEntity::updatePower);
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return world.getBlockEntity(pos, MinionBlocks.MINION_TRIGGER_BE_TYPE).map(MinionTriggerBlockEntity::getComparatorOutput).orElse(0);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MinionTriggerBlockEntity(pos, state);
    }

    @Override
    protected BlockEntityType<MinionTriggerBlockEntity> getBlockEntityType() {
        return MinionBlocks.MINION_TRIGGER_BE_TYPE;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return VersionSync.isOnClient(context) ? state : net.minecraft.world.level.block.Blocks.COMPARATOR.defaultBlockState().setValue(DiodeBlock.POWERED, state.getValue(POWERED));
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
        stack.set(DataComponents.ITEM_MODEL, ResourceLocation.fromNamespaceAndPath(Minions.MOD_ID, "minion_trigger_no_plate_" + (initialBlockState.getValue(MinionTriggerBlock.POWERED) ? "active" : "inactive")));

        ItemDisplayElement element = new ItemDisplayElement(stack);
        element.setItemDisplayContext(ItemDisplayContext.NONE);
        holder.addElement(element);
        return holder;
    }
}

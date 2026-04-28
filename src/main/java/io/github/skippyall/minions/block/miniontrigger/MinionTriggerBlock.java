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
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public class MinionTriggerBlock extends InstructionBoundBlock implements PolymerBlock, PolymerKeepModel, PolymerClientDecoded, BlockWithElementHolder {
    public static final MapCodec<MinionTriggerBlock> CODEC = createCodec(MinionTriggerBlock::new);

    public static final BooleanProperty POWERED = BooleanProperty.of("powered");
    public static final VoxelShape SHAPE = Block.createColumnShape(16.0, 0.0, 2.0);

    public MinionTriggerBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(POWERED, false));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        return this.canPlaceAbove(world, blockPos, world.getBlockState(blockPos));
    }

    protected boolean canPlaceAbove(WorldView world, BlockPos pos, BlockState state) {
        return state.isSideSolid(world, pos, Direction.UP, SideShapeType.RIGID);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if(!canPlaceAt(state, world, pos)) {
            dropStacks(state, world, pos);
            world.removeBlock(pos, false);
            return;
        }

        boolean newPower = world.isReceivingRedstonePower(pos);
        if(state.get(POWERED) != newPower) {
            world.setBlockState(pos, state.with(POWERED, newPower));
            world.getBlockEntity(pos, MinionBlocks.MINION_TRIGGER_BE_TYPE).ifPresent(MinionTriggerBlockEntity::updatePower);
        }
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return world.getBlockEntity(pos, MinionBlocks.MINION_TRIGGER_BE_TYPE).map(MinionTriggerBlockEntity::getComparatorOutput).orElse(0);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MinionTriggerBlockEntity(pos, state);
    }

    @Override
    protected BlockEntityType<MinionTriggerBlockEntity> getBlockEntityType() {
        return MinionBlocks.MINION_TRIGGER_BE_TYPE;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return VersionSync.isOnClient(context) ? state : net.minecraft.block.Blocks.COMPARATOR.getDefaultState().with(AbstractRedstoneGateBlock.POWERED, state.get(POWERED));
    }

    @Override
    public boolean handleMiningOnServer(ItemStack tool, BlockState state, BlockPos pos, ServerPlayerEntity player) {
        return false;
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        ElementHolder holder = new ElementHolder() {
            @Override
            public boolean startWatching(ServerPlayNetworkHandler player) {
                if(PolymerResourcePackUtils.hasMainPack(player) && !VersionSync.isOnClient(player)) {
                    return super.startWatching(player);
                } else {
                    return false;
                }
            }
        };
        ItemStack stack = new ItemStack(Items.BARRIER);
        stack.set(DataComponentTypes.ITEM_MODEL, Identifier.of(Minions.MOD_ID, "minion_trigger_no_plate_" + (initialBlockState.get(MinionTriggerBlock.POWERED) ? "active" : "inactive")));

        ItemDisplayElement element = new ItemDisplayElement(stack);
        element.setItemDisplayContext(ItemDisplayContext.NONE);
        holder.addElement(element);
        return holder;
    }
}

package io.github.skippyall.minions.block;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import io.github.skippyall.minions.MinionRegistration;
import io.github.skippyall.minions.PlayerClipboardAttachment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Optional;

public class MinionTriggerBlock extends BlockWithEntity implements PolymerBlock {
    public static final MapCodec<MinionTriggerBlock> CODEC = createCodec(MinionTriggerBlock::new);

    public static final BooleanProperty POWERED = BooleanProperty.of("powered");
    public static final BooleanProperty RUNNING = BooleanProperty.of("running");

    public MinionTriggerBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(POWERED, false).with(RUNNING, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED, RUNNING);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        PlayerClipboardAttachment clipboard = player.getAttached(PlayerClipboardAttachment.TYPE);
        if(clipboard != null) {
            Optional<MinionTriggerBlockEntity> be = world.getBlockEntity(pos, MinionRegistration.MINION_TRIGGER_BE_TYPE);
            if(be.isPresent()) {
                be.get().setInstruction(clipboard.selectedMinion(), clipboard.selectedInstruction());
                player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), SoundCategory.BLOCKS, 1, 1);

                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        boolean newPower = world.isReceivingRedstonePower(pos);
        if(state.get(POWERED) != newPower) {
            world.setBlockState(pos, state.with(POWERED, newPower));
            world.getBlockEntity(pos, MinionRegistration.MINION_TRIGGER_BE_TYPE).ifPresent(MinionTriggerBlockEntity::updatePower);
        }
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return world.getBlockEntity(pos, MinionRegistration.MINION_TRIGGER_BE_TYPE).map(MinionTriggerBlockEntity::getComparatorOutput).orElse(0);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MinionTriggerBlockEntity(pos, state);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return state.get(POWERED) ? Blocks.REDSTONE_BLOCK.getDefaultState() : Blocks.GOLD_BLOCK.getDefaultState();
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if(type == MinionRegistration.MINION_TRIGGER_BE_TYPE) {
            return MinionTriggerBlockEntity::tick;
        } else {
            return null;
        }
    }
}

package io.github.skippyall.minions.block.instruction_bound;

import io.github.skippyall.minions.block.miniontrigger.MinionTriggerBlockEntity;
import io.github.skippyall.minions.clipboard.InstructionClipboard;
import io.github.skippyall.minions.minion.MinionPersistentState;
import io.github.skippyall.minions.registration.MinionBlocks;
import io.github.skippyall.minions.registration.MinionComponentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class InstructionBoundBlock extends Block implements EntityBlock {
    public InstructionBoundBlock(Properties settings) {
        super(settings);
    }

    protected abstract BlockEntityType<? extends InstructionBoundBlockEntity<?>> getBlockEntityType();

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if(stack.get(MinionComponentTypes.REFERENCE) instanceof InstructionClipboard instruction && player instanceof ServerPlayer serverPlayer) {
            world.getBlockEntity(pos, getBlockEntityType()).ifPresent(be -> {
                be.setInstruction(instruction.selectedMinion(), instruction.selectedInstruction());
                serverPlayer.connection.send(new ClientboundSoundPacket(SoundEvents.NOTE_BLOCK_CHIME, SoundSource.BLOCKS, pos.getX(), pos.getY(), pos.getZ(), 1, 1, 0));
                stack.shrink(1);
            });
            return InteractionResult.SUCCESS;
        }

        return super.useItemOn(stack, state, world, pos, player, hand, hit);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if(world.isClientSide()) {
            return InteractionResult.CONSUME;
        }

        world.getBlockEntity(pos, getBlockEntityType()).ifPresent(be -> {
            String name = MinionPersistentState.get(world.getServer()).getMinionData(be.getMinionUuid()).name();
            player.sendSystemMessage(Component.translatable("minions.reference.instruction.tooltip", be.getInstructionName(), name));
        });
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        super.affectNeighborsAfterRemoval(state, world, pos, moved);
        world.getBlockEntity(pos, MinionBlocks.MINION_TRIGGER_BE_TYPE).ifPresent(MinionTriggerBlockEntity::removeListener);
    }
}

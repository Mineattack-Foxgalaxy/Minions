package io.github.skippyall.minions.block.instruction_bound;

import io.github.skippyall.minions.block.miniontrigger.MinionTriggerBlockEntity;
import io.github.skippyall.minions.clipboard.InstructionClipboard;
import io.github.skippyall.minions.minion.MinionPersistentState;
import io.github.skippyall.minions.registration.MinionBlocks;
import io.github.skippyall.minions.registration.MinionComponentTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class InstructionBoundBlock extends Block implements BlockEntityProvider {
    public InstructionBoundBlock(Settings settings) {
        super(settings);
    }

    protected abstract BlockEntityType<? extends InstructionBoundBlockEntity<?>> getBlockEntityType();

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(stack.get(MinionComponentTypes.REFERENCE) instanceof InstructionClipboard instruction) {
            world.getBlockEntity(pos, getBlockEntityType()).ifPresent(be -> {
                be.setInstruction(instruction.selectedMinion(), instruction.selectedInstruction());
                player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), SoundCategory.BLOCKS, 1, 1);
                stack.decrement(1);
            });
            return ActionResult.SUCCESS;
        }

        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if(world.isClient()) {
            return ActionResult.CONSUME;
        }

        world.getBlockEntity(pos, getBlockEntityType()).ifPresent(be -> {
            String name = MinionPersistentState.get(world.getServer()).getMinionData(be.getMinionUuid()).name();
            player.sendMessage(Text.translatable("minions.reference.instruction.tooltip", name, be.getInstructionName()), true);
        });
        return ActionResult.SUCCESS;
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        super.onStateReplaced(state, world, pos, moved);
        world.getBlockEntity(pos, MinionBlocks.MINION_TRIGGER_BE_TYPE).ifPresent(MinionTriggerBlockEntity::removeListener);
    }
}

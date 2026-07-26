//partially code from https://github.com/gnembon/fabric-carpet (EntityPlayerActionPack)
package io.github.skippyall.minions.minion.program.instruction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.minion.fakeplayer.EntityPlayerActionPack;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.handler.ParameterValueList;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.registration.ExecutionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class MineBlockExecution implements InstructionExecution.Argumentless {
    public static final Codec<MineBlockExecution> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockPos.CODEC.fieldOf("currentBlock").forGetter(e -> Objects.requireNonNull(e.currentBlock)),
                    Codec.FLOAT.fieldOf("currentBlockDamage").forGetter(e -> e.currentBlockDamage)
            ).apply(instance, MineBlockExecution::new)
    );

    //only null when stopping immediately
    private @Nullable BlockPos currentBlock;
    private float currentBlockDamage = 0;
    private boolean first = true;
    private boolean done = false;
    private boolean success = false;

    public MineBlockExecution() {}

    public MineBlockExecution(BlockPos currentBlock, float currentBlockDamage) {
        this.currentBlock = currentBlock;
        this.currentBlockDamage = currentBlockDamage;
    }

    @Override
    public void start(Context context) {
        MinionFakePlayer player = context.getOrThrow(ExecutionContext.MINION_KEY);
        if(EntityPlayerActionPack.getTarget(player) instanceof BlockHitResult hit) {
            this.currentBlock = hit.getBlockPos();

            EntityPlayerActionPack ap = player.getMinionActionPack();
            if (ap.blockHitDelay > 0) {
                ap.blockHitDelay--;
                done = true;
                return;
            }
            if (player.blockActionRestricted(player.level(), hit.getBlockPos(), player.gameMode.getGameModeForPlayer())) {
                done = true;
            }
        } else {
            done = true;
        }
    }

    @Override
    public void tick(Context context) {
        if(done || currentBlock == null) {
            return;
        }

        MinionFakePlayer player = context.getOrThrow(ExecutionContext.MINION_KEY);
        EntityPlayerActionPack ap = player.getMinionActionPack();

        HitResult newHit = EntityPlayerActionPack.getTarget(player);
        if(!(newHit instanceof BlockHitResult newBlockHit)) {
            done = true;
            return;
        }

        BlockPos newPos = newBlockHit.getBlockPos();
        if(newPos.equals(currentBlock)) {
            done = true;
            return;
        }

        if (player.level().getBlockState(currentBlock).isAir()) {
            done = true;
            return;
        }
        BlockState state = player.level().getBlockState(currentBlock);
        boolean blockBroken = false;
        if (first) {
            first = false;
            player.gameMode.handleBlockBreakAction(currentBlock, ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, newBlockHit.getDirection(), player.level().getMaxY(), -1);
            boolean notAir = !state.isAir();
            if (notAir)
            {
                state.attack(player.level(), currentBlock, player);
            }
            if (notAir && state.getDestroyProgress(player, player.level(), currentBlock) >= 1)
            {
                //instamine??
                blockBroken = true;
            }
        } else {
            currentBlockDamage += state.getDestroyProgress(player, player.level(), currentBlock);
            if (currentBlockDamage >= 1) {
                player.gameMode.handleBlockBreakAction(currentBlock, ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, newBlockHit.getDirection(), player.level().getMaxY(), -1);
                ap.blockHitDelay = 5;
                blockBroken = true;
            }
            player.level().destroyBlockProgress(-1, currentBlock, (int) (currentBlockDamage * 10));

        }
        player.resetLastActionTime();
        player.swing(InteractionHand.MAIN_HAND);

        if(blockBroken) {
            done = true;
            success = true;
        }
    }

    @Override
    public boolean isDone(Context context) {
        return done;
    }

    @Override
    public void stop(ParameterValueList list, Context context) {
        MinionFakePlayer player = context.getOrThrow(ExecutionContext.MINION_KEY);
        EntityPlayerActionPack ap = player.getMinionActionPack();

        if(currentBlock != null) {
            player.level().destroyBlockProgress(-1, currentBlock, -1);
            player.gameMode.handleBlockBreakAction(currentBlock, ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, Direction.DOWN, player.level().getMaxY(), -1);
        }
    }
}

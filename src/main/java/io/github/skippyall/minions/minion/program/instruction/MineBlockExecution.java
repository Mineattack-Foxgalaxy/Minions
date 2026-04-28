//partially code from https://github.com/gnembon/fabric-carpet (EntityPlayerActionPack)
package io.github.skippyall.minions.minion.program.instruction;

import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.EntityPlayerActionPack;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.consumer.ValueConsumerList;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.program.supplier.ParameterValueList;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class MineBlockExecution implements InstructionExecution<MinionRuntime> {
    private BlockPos currentBlock;
    private float currentBlockDamage = 0;
    private boolean first = true;
    private boolean done = false;
    private boolean success = false;

    @Override
    public void start(MinionRuntime runtime) {
        MinionFakePlayer player = runtime.getMinion();
        if(EntityPlayerActionPack.getTarget(player) instanceof BlockHitResult hit) {
            this.currentBlock = hit.getBlockPos();

            EntityPlayerActionPack ap = player.getMinionActionPack();
            if (ap.blockHitDelay > 0) {
                ap.blockHitDelay--;
                done = true;
                return;
            }
            if (player.isBlockBreakingRestricted(player.getWorld(), hit.getBlockPos(), player.interactionManager.getGameMode())) {
                done = true;
                return;
            }
        } else {
            done = true;
        }
    }

    @Override
    public void tick(MinionRuntime runtime) {
        if(done) {
            return;
        }

        MinionFakePlayer player = runtime.getMinion();
        EntityPlayerActionPack ap = player.getMinionActionPack();

        HitResult newHit = EntityPlayerActionPack.getTarget(player);
        if(!(newHit instanceof BlockHitResult newBlockHit)) {
            done = true;
            return;
        }

        BlockPos newPos = newBlockHit.getBlockPos();
        if(!newPos.equals(currentBlock)) {
            done = true;
            return;
        }

        if (player.getWorld().getBlockState(currentBlock).isAir()) {
            done = true;
            return;
        }
        BlockState state = player.getWorld().getBlockState(currentBlock);
        boolean blockBroken = false;
        if (first) {
            first = false;
            player.interactionManager.processBlockBreakingAction(currentBlock, PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, newBlockHit.getSide(), player.getWorld().getTopYInclusive(), -1);
            boolean notAir = !state.isAir();
            if (notAir)
            {
                state.onBlockBreakStart(player.getWorld(), currentBlock, player);
            }
            if (notAir && state.calcBlockBreakingDelta(player, player.getWorld(), currentBlock) >= 1)
            {
                //instamine??
                blockBroken = true;
            }
        } else {
            currentBlockDamage += state.calcBlockBreakingDelta(player, player.getWorld(), currentBlock);
            if (currentBlockDamage >= 1) {
                player.interactionManager.processBlockBreakingAction(currentBlock, PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, newBlockHit.getSide(), player.getWorld().getTopYInclusive(), -1);
                ap.blockHitDelay = 5;
                blockBroken = true;
            }
            player.getWorld().setBlockBreakingInfo(-1, currentBlock, (int) (currentBlockDamage * 10));

        }
        player.updateLastActionTime();
        player.swingHand(Hand.MAIN_HAND);

        if(blockBroken) {
            done = true;
            success = true;
        }
    }

    @Override
    public boolean isDone(MinionRuntime runtime) {
        return done;
    }

    @Override
    public void stop(MinionRuntime runtime, ValueConsumerList<MinionRuntime> valueConsumers) {
        MinionFakePlayer player = runtime.getMinion();
        EntityPlayerActionPack ap = player.getMinionActionPack();

        if(currentBlock != null) {
            player.getWorld().setBlockBreakingInfo(-1, currentBlock, -1);
            player.interactionManager.processBlockBreakingAction(currentBlock, PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, Direction.DOWN, player.getWorld().getTopYInclusive(), -1);
        }
    }

    @Override
    public void readArguments(ParameterValueList arguments, MinionRuntime runtime) {

    }

    @Override
    public void save(WriteView view, MinionRuntime runtime) {
        view.put("currentBlock", BlockPos.CODEC, currentBlock);
        view.putFloat("currentBlockDamage", currentBlockDamage);
    }

    @Override
    public void load(ReadView view, MinionRuntime runtime) {
        currentBlock = view.read("currentBlock", BlockPos.CODEC).orElse(null);
        currentBlockDamage = view.getFloat("currentBlockDamage", 0);
        if(currentBlock == null) {
            done = true;
        }
    }
}

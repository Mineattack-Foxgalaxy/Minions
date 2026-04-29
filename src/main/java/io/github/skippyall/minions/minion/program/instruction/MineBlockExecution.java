//partially code from https://github.com/gnembon/fabric-carpet (EntityPlayerActionPack)
package io.github.skippyall.minions.minion.program.instruction;

import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.EntityPlayerActionPack;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.consumer.ValueConsumerList;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.program.supplier.ParameterValueList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

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
            if (player.blockActionRestricted(player.level(), hit.getBlockPos(), player.gameMode.getGameModeForPlayer())) {
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
    public boolean isDone(MinionRuntime runtime) {
        return done;
    }

    @Override
    public void stop(MinionRuntime runtime, ValueConsumerList<MinionRuntime> valueConsumers) {
        MinionFakePlayer player = runtime.getMinion();
        EntityPlayerActionPack ap = player.getMinionActionPack();

        if(currentBlock != null) {
            player.level().destroyBlockProgress(-1, currentBlock, -1);
            player.gameMode.handleBlockBreakAction(currentBlock, ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, Direction.DOWN, player.level().getMaxY(), -1);
        }
    }

    @Override
    public void readArguments(ParameterValueList arguments, MinionRuntime runtime) {

    }

    @Override
    public void save(ValueOutput view, MinionRuntime runtime) {
        view.store("currentBlock", BlockPos.CODEC, currentBlock);
        view.putFloat("currentBlockDamage", currentBlockDamage);
    }

    @Override
    public void load(ValueInput view, MinionRuntime runtime) {
        currentBlock = view.read("currentBlock", BlockPos.CODEC).orElse(null);
        currentBlockDamage = view.getFloatOr("currentBlockDamage", 0);
        if(currentBlock == null) {
            done = true;
        }
    }
}

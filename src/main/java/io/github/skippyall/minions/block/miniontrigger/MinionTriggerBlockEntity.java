package io.github.skippyall.minions.block.miniontrigger;

import io.github.skippyall.minions.GlobalInstructionManager;
import io.github.skippyall.minions.gui.instruction.ConfigureInstructionGui;
import io.github.skippyall.minions.gui.instruction.InstructionGui;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.instruction.ExecutingInstruction;
import io.github.skippyall.minions.program.supplier.ParameterValueList;
import io.github.skippyall.minions.registration.MinionBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.OptionalInt;
import java.util.UUID;

public class MinionTriggerBlockEntity extends BlockEntity {
    private @Nullable UUID minionUuid;
    private @Nullable ConfiguredInstruction<MinionRuntime> instruction;
    private boolean running = false;
    private int instructionId;

    public MinionTriggerBlockEntity(BlockPos pos, BlockState state) {
        super(MinionBlocks.MINION_TRIGGER_BE_TYPE, pos, state);
    }

    public boolean openGui(ServerPlayer player) {
        if(instruction != null) {
            new ConfigureInstructionGui(player, instruction);
            return true;
        } else if(minionUuid != null) {
            if(level.getServer().getPlayerList().getPlayer(minionUuid) instanceof MinionFakePlayer minion) {
                InstructionGui.selectInstructionModuleMenu(player, null, minion)
                        .thenAccept(type -> {
                            instruction = new ConfiguredInstruction<>(type);
                        });
            }
            return true;
        }
        return false;
    }

    public void updatePower() {
        boolean powered = getBlockState().getValue(MinionTriggerBlock.POWERED);

        if(instruction != null && minionUuid != null) {
            if (powered) {
                if (level.getServer().getPlayerList().getPlayer(minionUuid) instanceof MinionFakePlayer minion) {
                    OptionalInt id = instruction.run(minion.getRuntime());
                    if (id.isPresent()) {
                        running = true;
                        this.instructionId = id.getAsInt();
                        GlobalInstructionManager.get(level.getServer()).getInstruction(instructionId).addListener(new ExecutingInstruction.Listener() {
                            @Override
                            public void onStop(InstructionRuntime<?> runtime, ParameterValueList returnValues) {
                                ExecutingInstruction.Listener.super.onStop(runtime, returnValues);
                            }
                        });
                    }
                }
            } else {
                ExecutingInstruction<?> instruction = GlobalInstructionManager.get(level.getServer()).getInstruction(instructionId);
                if(instruction != null) {
                    instruction.stop();
                }
                onStop();
            }
        }
    }

    public int getComparatorOutput() {
        if(running) {
            return 15;
        }
        return 0;
    }

    public void checkStop() {
        if(!running) {
            return;
        }
        ExecutingInstruction<?> executingInstruction = getExecutingInstruction();
        if(executingInstruction.getState() == ExecutingInstruction.State.STOPPED) {
            onStop();
        }
    }

    public void onStop() {
        running = false;
        level.updateNeighbourForOutputSignal(worldPosition, MinionBlocks.MINION_TRIGGER_BLOCK);
    }

    public @Nullable ExecutingInstruction<?> getExecutingInstruction() {
        if(level.isClientSide() || !running) {
            return null;
        }
        return GlobalInstructionManager.get(level.getServer()).getInstruction(instructionId);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if(!level.isClientSide()) {
            level.getServer().execute(this::checkStop);
        }
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        minionUuid = view.read("minionUuid", UUIDUtil.AUTHLIB_CODEC).orElse(null);
        instructionId = view.getIntOr("instructionId", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        if(minionUuid != null) {
            view.store("minionUuid", UUIDUtil.AUTHLIB_CODEC, minionUuid);
        }
        view.putInt("instructionId", instructionId);
    }
}

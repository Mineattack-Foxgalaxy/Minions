package io.github.skippyall.minions.block.miniontrigger;

import io.github.skippyall.minions.GlobalInstructionManager;
import io.github.skippyall.minions.gui.instruction.ConfigureInstructionGui;
import io.github.skippyall.minions.gui.instruction.InstructionGui;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.ExecutionContext;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.instruction.ExecutingInstruction;
import io.github.skippyall.minions.program.supplier.ParameterValueList;
import io.github.skippyall.minions.registration.MinionBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
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
    private @Nullable ConfiguredInstruction instruction;
    private boolean running = false;
    private int instructionId = -1;

    public MinionTriggerBlockEntity(BlockPos pos, BlockState state) {
        super(MinionBlocks.MINION_TRIGGER_BE_TYPE, pos, state);
    }

    public void setMinion(UUID minionUuid) {
        stop();
        this.minionUuid = minionUuid;
    }

    public boolean openGui(ServerPlayer player) {
        if(instruction != null) {
            new ConfigureInstructionGui(player, null, instruction, () -> {
                stop();
                instruction = null;
            });
            return true;
        } else if(minionUuid != null) {
            if(level instanceof ServerLevel serverLevel && serverLevel.getServer().getPlayerList().getPlayer(minionUuid) instanceof MinionFakePlayer minion) {
                InstructionGui.selectInstructionModuleMenu(player, null, minion)
                        .thenAccept(type -> {
                            instruction = new ConfiguredInstruction(type);
                        });
            }
            return true;
        }
        return false;
    }

    public void updatePower() {
        boolean powered = getBlockState().getValue(MinionTriggerBlock.POWERED);

        if(instruction != null && minionUuid != null && level instanceof ServerLevel serverLevel) {
            if (powered) {
                start();
            } else {
                stop();
            }
        }
    }

    public int getComparatorOutput() {
        if(running) {
            return 15;
        }
        return 0;
    }

    public void start() {
        if(instruction != null && minionUuid != null && level instanceof ServerLevel serverLevel) {
            if (serverLevel.getServer().getPlayerList().getPlayer(minionUuid) instanceof MinionFakePlayer minion) {
                OptionalInt id = minion.getRuntime().run(instruction);
                if (id.isPresent()) {
                    running = true;
                    this.instructionId = id.getAsInt();
                    GlobalInstructionManager.get(serverLevel.getServer()).getInstruction(minionUuid, instructionId).addListener(new MinionTriggerInstructionListener(level.dimension(), worldPosition));
                }
            }
        }
    }

    public void stop() {
        if(instruction != null && minionUuid != null && level instanceof ServerLevel serverLevel) {
            ExecutingInstruction instruction = GlobalInstructionManager.get(serverLevel.getServer()).getInstruction(minionUuid, instructionId);
            if (instruction != null) {
                instruction.scheduleStop();
            }
            onStop();
        }
    }

    public void checkStop() {
        ExecutingInstruction executingInstruction = getExecutingInstruction();
        if(executingInstruction != null && executingInstruction.getState() == ExecutingInstruction.State.STOPPED) {
            onStop();
        }
    }

    public void onStop() {
        running = false;
        if(level != null) {
            level.updateNeighbourForOutputSignal(worldPosition, MinionBlocks.MINION_TRIGGER_BLOCK);
        }
    }

    public @Nullable ExecutingInstruction getExecutingInstruction() {
        if(level instanceof ServerLevel serverLevel && running) {
            return GlobalInstructionManager.get(serverLevel.getServer()).getInstruction(minionUuid, instructionId);
        } else {
            return null;
        }
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if(level instanceof ServerLevel serverLevel) {
            serverLevel.getServer().execute(this::checkStop);
        }
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        minionUuid = view.read("minionUuid", UUIDUtil.AUTHLIB_CODEC).orElse(null);
        running = view.getBooleanOr("running", false);
        instructionId = view.getIntOr("instructionId", -1);
        instruction = view.read("instruction", ConfiguredInstruction.CODEC).orElse(null);
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        if(minionUuid != null) {
            view.store("minionUuid", UUIDUtil.AUTHLIB_CODEC, minionUuid);
        }
        view.putBoolean("running", running);
        view.putInt("instructionId", instructionId);
        if(instruction != null) {
            view.store("instruction", ConfiguredInstruction.CODEC, instruction);
        }
    }

    public static class MinionTriggerInstructionListener implements ExecutingInstruction.Listener {
        private final ResourceKey<Level> dimension;
        private final BlockPos pos;

        public MinionTriggerInstructionListener(ResourceKey<Level> dimension, BlockPos pos) {
            this.dimension = dimension;
            this.pos = pos;
        }

        @Override
        public void onStop(ExecutionContext context, ParameterValueList returnValues) {
            MinionFakePlayer minion = context.get(MinionRuntime.MINION_KEY);
            if(minion != null) {
                ServerLevel level = minion.getServer().getLevel(dimension);
                if(level != null) {
                    level.getBlockEntity(pos, MinionBlocks.MINION_TRIGGER_BE_TYPE).ifPresent(MinionTriggerBlockEntity::checkStop);
                }
            }
        }
    }
}

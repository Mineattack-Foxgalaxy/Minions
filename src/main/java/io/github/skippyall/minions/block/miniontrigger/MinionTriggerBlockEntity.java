package io.github.skippyall.minions.block.miniontrigger;

import com.mojang.datafixers.util.Pair;
import io.github.skippyall.minions.GlobalInstructionManager;
import io.github.skippyall.minions.block.input.ValueProvider;
import io.github.skippyall.minions.gui.instruction.ConfigureInstructionGui;
import io.github.skippyall.minions.gui.instruction.InstructionGui;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.instruction.ExecutingInstruction;
import io.github.skippyall.minions.program.handler.Parameter;
import io.github.skippyall.minions.program.handler.ParameterValueList;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.registration.ExecutionContext;
import io.github.skippyall.minions.registration.MinionBlocks;
import io.github.skippyall.minions.registration.ResolutionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.OptionalInt;
import java.util.UUID;

public class MinionTriggerBlockEntity extends BlockEntity {
    private final Context resolutionContext = Context.builder()
            .put(ResolutionContext.MINION_TRIGGER, this)
            .build();

    private Map<String, Pair<BlockPos, Direction>> connectedParamBlocks = new HashMap<>();

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
            }, resolutionContext);
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
                updateConnectedBlocks();
                OptionalInt id = instruction.run(minion.getRuntime(), resolutionContext);
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

    public void updateConnectedBlocks() {
        Collection<Pair<BlockPos, Direction>> connectedBlocks = findConnectedBlocks(level, worldPosition, 16, MinionBlocks.CONNECTOR);

        Collection<String> requiredParams = new HashSet<>();
        for(Parameter<?> parameter : instruction.getInstruction().getParameters()) {
            requiredParams.add(parameter.name());
        }

        for(Pair<BlockPos, Direction> pos : connectedBlocks) {
            String signText = findSignText(level, pos.getFirst());
            if(signText != null && requiredParams.contains(signText)) {
                connectedParamBlocks.put(signText, pos);
                requiredParams.remove(signText);
            }
        }
    }

    public @Nullable TypedValue<?> getValue(String paramName) {
        Pair<BlockPos, Direction> pos = connectedParamBlocks.get(paramName);
        if(pos != null && level != null) {
            ValueProvider provider = ValueProvider.SIDED.find(level, pos.getFirst(), pos.getSecond());
            if (provider != null) {
                return provider.getValue();
            }
        }
        return null;
    }

    public static @Nullable String findSignText(Level level, BlockPos pos) {
        for(Direction dir : Direction.values()) {
            BlockPos neighbor = pos.relative(dir);
            BlockState state = level.getBlockState(neighbor);
            if(state.getBlock() instanceof SignBlock block && level.getBlockEntity(neighbor) instanceof SignBlockEntity be) {
                StringBuilder text = new StringBuilder();
                for(Component partialText : be.getFrontText().getMessages(false)) {
                    text.append(partialText.getString().strip());
                }
                return text.toString();
            }
        }
        return null;
    }

    public static Collection<Pair<BlockPos, Direction>> findConnectedBlocks(Level level, BlockPos startPos, int range, Block connectorBlock) {
        Collection<BlockPos> visitedPositions = new HashSet<>();
        Collection<BlockPos> currentPositions = new ArrayList<>();
        Collection<BlockPos> newPositions = new HashSet<>();
        Collection<Pair<BlockPos, Direction>> foundPositions = new LinkedHashSet<>();

        currentPositions.add(startPos);

        for(int i = 0; i < range && !currentPositions.isEmpty(); i++) {
            visitedPositions.addAll(currentPositions);

            for(BlockPos pos : currentPositions) {
                for(Direction dir : Direction.values()) {
                    BlockPos newPos = pos.relative(dir);
                    if(!visitedPositions.contains(newPos)) {
                        if (level.getBlockState(newPos).getBlock() == connectorBlock) {
                            newPositions.add(newPos);
                        } else if (ValueProvider.SIDED.find(level, newPos, dir.getOpposite()) != null) {
                            foundPositions.add(Pair.of(newPos, dir));
                        }
                    }
                }
            }
            currentPositions.clear();
            currentPositions.addAll(newPositions);
            newPositions.clear();
        }
        return foundPositions;
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
        public void onStop(Context context, ParameterValueList returnValues) {
            MinionFakePlayer minion = context.get(ExecutionContext.MINION_KEY);
            if(minion != null) {
                ServerLevel level = minion.getServer().getLevel(dimension);
                if(level != null) {
                    level.getBlockEntity(pos, MinionBlocks.MINION_TRIGGER_BE_TYPE).ifPresent(MinionTriggerBlockEntity::checkStop);
                }
            }
        }
    }
}

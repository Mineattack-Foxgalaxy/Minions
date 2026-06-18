package io.github.skippyall.minions.block.miniontrigger;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.listener.BlockEntityMinionInstructionListener;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.instruction.ExecutingInstruction;
import io.github.skippyall.minions.program.supplier.ParameterValueList;
import io.github.skippyall.minions.registration.MinionBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/*public class MinionTriggerMinionListener extends BlockEntityMinionInstructionListener<MinionTriggerBlockEntity> {
    public static final Codec<MinionTriggerMinionListener> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Level.RESOURCE_KEY_CODEC.fieldOf("world").forGetter(listener -> listener.worldKey),
                    BlockPos.CODEC.fieldOf("pos").forGetter(listener -> listener.pos),
                    UUIDUtil.AUTHLIB_CODEC.fieldOf("minionUuid").forGetter(listener -> listener.minionUuid),
                    Codec.INT.fieldOf("instructionId").forGetter(listener -> listener.instructionId)
            ).apply(instance, MinionTriggerMinionListener::new));

    int instructionId;
    final TriggerInstructionListener listener = new TriggerInstructionListener();

    boolean runningCache;
    boolean incomingPowerCache;

    MinionTriggerMinionListener(ResourceKey<Level> worldKey, BlockPos pos, UUID minionUuid, int instructionId) {
        super(worldKey, pos, minionUuid, MinionBlocks.MINION_TRIGGER_BE_TYPE);
        this.instructionId = instructionId;
    }

    @Override
    protected Map<Integer, ExecutingInstruction.Listener> getInstructionListeners() {
        return Map.of(instructionId, listener);
    }

    @Override
    public void onMinionSpawn(MinionFakePlayer minion) {
        super.onMinionSpawn(minion);
        updateComparatorsIfLoaded(minion.getServer());

        ExecutingInstruction<MinionRuntime> instruction = minion.getRuntime().getInstruction(instructionId);
        if(instruction.isRunning() && !incomingPowerCache) {
            instruction.stop(minion.getRuntime());
        }
    }

    @Override
    public void onMinionRemove(MinionFakePlayer minion) {
        super.onMinionRemove(minion);
        runningCache = false;
        updateComparatorsIfLoaded(minion.getServer());
    }

    @Override
    public void add(MinecraftServer server) {
        super.add(server);
        runningCache = minion.getRuntime().getInstruction(instructionId).isRunning();
        updateComparatorsIfLoaded(server);
    }

    @Override
    public Optional<Identifier> getCodecId() {
        return Optional.of(Identifier.fromNamespaceAndPath(Minions.MOD_ID, "minion_trigger"));
    }

    public void updateComparatorsIfLoaded(MinecraftServer server) {
        Level world = server.getLevel(worldKey);
        if(world.isLoaded(pos)) {
            world.updateNeighbourForOutputSignal(pos, MinionBlocks.MINION_TRIGGER_BLOCK);
        }
    }

    public boolean isRunning() {
        return runningCache;
    }

    public class TriggerInstructionListener implements ExecutingInstruction.Listener {
        @Override
        public void onStop(InstructionRuntime<?> runtime, ParameterValueList returnValues) {
            runningCache = false;
            updateComparatorsIfLoaded(minion.getServer());
        }
    }
}*/

package io.github.skippyall.minions.block.miniontrigger;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.listener.BlockEntityMinionInstructionListener;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.instruction.ConfiguredInstructionListener;
import io.github.skippyall.minions.registration.MinionBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class MinionTriggerMinionListener extends BlockEntityMinionInstructionListener<MinionTriggerBlockEntity> {
    public static final Codec<MinionTriggerMinionListener> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Level.RESOURCE_KEY_CODEC.fieldOf("world").forGetter(listener -> listener.worldKey),
                    BlockPos.CODEC.fieldOf("pos").forGetter(listener -> listener.pos),
                    UUIDUtil.AUTHLIB_CODEC.fieldOf("minionUuid").forGetter(listener -> listener.minionUuid),
                    Codec.STRING.fieldOf("instructionName").forGetter(listener -> listener.instructionName)
            ).apply(instance, MinionTriggerMinionListener::new));

    String instructionName;
    final TriggerInstructionListener listener = new TriggerInstructionListener();

    boolean runningCache;
    boolean incomingPowerCache;

    MinionTriggerMinionListener(ResourceKey<Level> worldKey, BlockPos pos, UUID minionUuid, String instructionName) {
        super(worldKey, pos, minionUuid, MinionBlocks.MINION_TRIGGER_BE_TYPE);
        this.instructionName = Objects.requireNonNull(instructionName);
    }

    @Override
    protected Map<String, ConfiguredInstructionListener> getInstructionListeners() {
        return Map.of(instructionName, listener);
    }

    @Override
    public void onMinionSpawn(MinionFakePlayer minion) {
        super.onMinionSpawn(minion);
        runningCache = minion.getInstructionManager().getInstruction(instructionName).isRunning();
        updateComparatorsIfLoaded(minion.getServer());

        ConfiguredInstruction<MinionRuntime> instruction = minion.getInstructionManager().getInstruction(instructionName);
        if(instruction.isRunning() && !incomingPowerCache) {
            instruction.stop(minion.getInstructionManager());
        } else if (!instruction.isRunning() && incomingPowerCache) {
            instruction.run(minion.getInstructionManager());
        }
    }

    @Override
    public void onMinionRemove(MinionFakePlayer minion) {
        super.onMinionRemove(minion);
        runningCache = false;
        updateComparatorsIfLoaded(minion.getServer());
    }

    @Override
    public void onInstructionRename(MinionFakePlayer minion, ConfiguredInstruction<?> instruction, String oldName, String newName) {
        super.onInstructionRename(minion, instruction, oldName, newName);
        if(instructionName.equals(oldName)) {
            instructionName = newName;
        }
    }

    @Override
    public void add(MinecraftServer server) {
        super.add(server);
        runningCache = minion.getInstructionManager().getInstruction(instructionName).isRunning();
        updateComparatorsIfLoaded(server);
    }

    @Override
    public Optional<ResourceLocation> getCodecId() {
        return Optional.of(ResourceLocation.fromNamespaceAndPath(Minions.MOD_ID, "minion_trigger"));
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

    public class TriggerInstructionListener implements ConfiguredInstructionListener {
        @Override
        public void onRun(ConfiguredInstruction<?> instruction) {
            runningCache = true;
            updateComparatorsIfLoaded(minion.getServer());
        }

        @Override
        public void onStop(ConfiguredInstruction<?> instruction) {
            runningCache = false;
            updateComparatorsIfLoaded(minion.getServer());
        }
    }
}

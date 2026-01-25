package io.github.skippyall.minions.block.miniontrigger;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.listener.BlockEntityMinionInstructionListener;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.registration.MinionBlocks;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.instruction.ConfiguredInstructionListener;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class MinionTriggerMinionListener extends BlockEntityMinionInstructionListener<MinionTriggerBlockEntity> {
    public static final Codec<MinionTriggerMinionListener> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    World.CODEC.fieldOf("world").forGetter(listener -> listener.worldKey),
                    BlockPos.CODEC.fieldOf("pos").forGetter(listener -> listener.pos),
                    Uuids.CODEC.fieldOf("minionUuid").forGetter(listener -> listener.minionUuid),
                    Codec.STRING.fieldOf("instructionName").forGetter(listener -> listener.instructionName)
            ).apply(instance, MinionTriggerMinionListener::new));

    String instructionName;
    final TriggerInstructionListener listener = new TriggerInstructionListener();

    boolean runningCache;
    boolean incomingPowerCache;

    private MinionTriggerMinionListener(RegistryKey<World> worldKey, BlockPos pos, UUID minionUuid, String instructionName) {
        super(worldKey, pos, minionUuid, MinionBlocks.MINION_TRIGGER_BE_TYPE);
        this.instructionName = Objects.requireNonNull(instructionName);
    }

    public static void addListener(World world, BlockPos pos, UUID minion, String instructionName) {
        MinionTriggerMinionListener listener = new MinionTriggerMinionListener(world.getRegistryKey(), pos, minion, instructionName);
        listener.add(world.getServer());
    }

    public static void removeListener(World world, BlockPos pos, UUID minion, String instructionName) {
        MinionTriggerMinionListener old = getListener(world, pos, minion, MinionTriggerMinionListener.class);
        if(old != null) {
            old.remove(world.getServer());
        }
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
    protected void add(MinecraftServer server) {
        super.add(server);
        runningCache = minion.getInstructionManager().getInstruction(instructionName).isRunning();
        updateComparatorsIfLoaded(server);
    }

    @Override
    public Optional<Identifier> getCodecId() {
        return Optional.of(Identifier.of(Minions.MOD_ID, "minion_trigger"));
    }

    public void updateComparatorsIfLoaded(MinecraftServer server) {
        World world = server.getWorld(worldKey);
        if(world.isPosLoaded(pos)) {
            world.updateComparators(pos, MinionBlocks.MINION_TRIGGER_BLOCK);
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

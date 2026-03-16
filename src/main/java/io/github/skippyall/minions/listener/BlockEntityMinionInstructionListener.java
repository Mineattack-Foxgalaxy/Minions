package io.github.skippyall.minions.listener;

import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.ConfiguredInstructionListener;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.UUID;

public abstract class BlockEntityMinionInstructionListener<E extends BlockEntity> extends BlockEntityMinionListener<E> {
    protected BlockEntityMinionInstructionListener(RegistryKey<World> worldKey, BlockPos pos, UUID minionUuid, BlockEntityType<E> type) {
        super(worldKey, pos, minionUuid, type);
    }

    protected abstract Map<String, ConfiguredInstructionListener> getInstructionListeners();

    @Override
    public void onMinionSpawn(MinionFakePlayer minion) {
        super.onMinionSpawn(minion);
        registerInstructionListeners();
    }

    @Override
    public void add(MinecraftServer server) {
        super.add(server);
        if(minion != null) {
            registerInstructionListeners();
        }
    }

    @Override
    public void remove(MinecraftServer server) {
        super.remove(server);
        if(minion != null) {
            removeInstructionListeners();
        }
    }

    public void registerInstructionListeners() {
        for(Map.Entry<String, ConfiguredInstructionListener> listener : getInstructionListeners().entrySet()) {
            minion.getInstructionManager().getInstruction(listener.getKey()).addListener(listener.getValue());
        }
    }

    public void removeInstructionListeners() {
        for(Map.Entry<String, ConfiguredInstructionListener> listener : getInstructionListeners().entrySet()) {
            minion.getInstructionManager().getInstruction(listener.getKey()).removeListener(listener.getValue());
        }
    }
}

package io.github.skippyall.minions.listener;

import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.ExecutingInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import java.util.Map;
import java.util.UUID;

public abstract class BlockEntityMinionInstructionListener<E extends BlockEntity> extends BlockEntityMinionListener<E> {
    protected BlockEntityMinionInstructionListener(ResourceKey<Level> worldKey, BlockPos pos, UUID minionUuid, BlockEntityType<E> type) {
        super(worldKey, pos, minionUuid, type);
    }

    protected abstract Map<Integer, ExecutingInstruction.Listener> getInstructionListeners();

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
        for(Map.Entry<Integer, ExecutingInstruction.Listener> listener : getInstructionListeners().entrySet()) {
            minion.getRuntime().getInstruction(listener.getKey()).addListener(listener.getValue());
        }
    }

    public void removeInstructionListeners() {
        for(Map.Entry<Integer, ExecutingInstruction.Listener> listener : getInstructionListeners().entrySet()) {
            minion.getRuntime().getInstruction(listener.getKey()).removeListener(listener.getValue());
        }
    }
}

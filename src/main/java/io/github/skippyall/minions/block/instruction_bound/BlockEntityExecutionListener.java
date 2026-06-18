package io.github.skippyall.minions.block.instruction_bound;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.instruction.ExecutingInstruction;
import io.github.skippyall.minions.program.supplier.ParameterValueList;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public class BlockEntityExecutionListener implements ExecutingInstruction.Listener {
    public static final Codec<BlockEntityExecutionListener> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Level.RESOURCE_KEY_CODEC.fieldOf("levelId").forGetter(l -> l.levelId),
                    BlockPos.CODEC.fieldOf("pos").forGetter(l -> l.pos)
            ).apply(instance, BlockEntityExecutionListener::new));
    public static final Identifier CODEC_ID = Identifier.fromNamespaceAndPath(Minions.MOD_ID, "block_entity");

    private final ResourceKey<Level> levelId;
    private final BlockPos pos;

    public BlockEntityExecutionListener(ResourceKey<Level> level, BlockPos pos) {
        this.levelId = level;
        this.pos = pos;
    }

    @Override
    public Optional<Identifier> getCodecId() {
        return Optional.of(CODEC_ID);
    }

    public ExecutingInstruction.@Nullable Listener getDelegate(InstructionRuntime<?> runtime) {
        if(runtime instanceof MinionRuntime minionRuntime) {
            Level level = minionRuntime.getMinion().getServer().getLevel(levelId);
            if (level != null && level.isLoaded(pos) && level.getBlockEntity(pos) instanceof ListenerProvider provider) {
                return provider.getListener();
            }
        }
        return null;
    }

    private void ifDelegatePresent(InstructionRuntime<?> runtime, Consumer<ExecutingInstruction.Listener> listenerConsumer) {
        ExecutingInstruction.Listener delegate = getDelegate(runtime);
        if(delegate != null) {
            listenerConsumer.accept(delegate);
        }
    }

    @Override
    public void onStop(InstructionRuntime<?> runtime, ParameterValueList returnValues) {
        ifDelegatePresent(runtime, d -> d.onStop(runtime, returnValues));
    }

    public interface ListenerProvider {
        ExecutingInstruction.Listener getListener();
    }
}

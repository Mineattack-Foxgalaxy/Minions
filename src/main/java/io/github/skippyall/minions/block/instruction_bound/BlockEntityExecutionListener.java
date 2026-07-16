package io.github.skippyall.minions.block.instruction_bound;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.instruction.ExecutingInstruction;
import io.github.skippyall.minions.program.supplier.ParameterValueList;
import io.github.skippyall.minions.registration.ExecutionContext;
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

    public ExecutingInstruction.@Nullable Listener getDelegate(Context context) {
        if(context.get(ExecutionContext.MINION_KEY) != null) {
            Level level = context.getOrThrow(ExecutionContext.MINION_KEY).getServer().getLevel(levelId);
            if (level != null && level.isLoaded(pos) && level.getBlockEntity(pos) instanceof ListenerProvider provider) {
                return provider.getListener();
            }
        }
        return null;
    }

    private void ifDelegatePresent(Context context, Consumer<ExecutingInstruction.Listener> listenerConsumer) {
        ExecutingInstruction.Listener delegate = getDelegate(context);
        if(delegate != null) {
            listenerConsumer.accept(delegate);
        }
    }

    @Override
    public void onStop(Context context, ParameterValueList returnValues) {
        ifDelegatePresent(context, d -> d.onStop(context, returnValues));
    }

    public interface ListenerProvider {
        ExecutingInstruction.Listener getListener();
    }
}

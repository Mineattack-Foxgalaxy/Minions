package io.github.skippyall.minions.minion.program.instruction.inventory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.handler.Parameter;
import io.github.skippyall.minions.program.handler.ParameterValueList;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.registration.ExecutionContext;
import io.github.skippyall.minions.registration.ValueTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class GetStackExecution implements InstructionExecution {
    public static final Codec<GetStackExecution> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("slot").forGetter(e -> e.slot),
                    Codec.BOOL.fieldOf("screen").forGetter(e -> e.screen),
                    ItemStack.OPTIONAL_CODEC.optionalFieldOf("stack").forGetter(e -> Optional.ofNullable(e.stack))
            ).apply(instance, GetStackExecution::new)
    );

    public static final Parameter<Long> SLOT = new Parameter<>("slot", ValueTypes.LONG);
    public static final Parameter<Boolean> SCREEN = new Parameter<>("screen", ValueTypes.BOOLEAN);

    public static final Parameter<String> ID = new Parameter<>("id", ValueTypes.STRING);
    public static final Parameter<Long> COUNT = new Parameter<>("count", ValueTypes.LONG);

    private int slot;
    private boolean screen;

    private @Nullable ItemStack stack;

    public GetStackExecution(ParameterValueList arguments, Context context) {
        slot = arguments.getValue(SLOT).intValue();
        screen = arguments.getValue(SCREEN);
    }

    public GetStackExecution(int slot, boolean screen, Optional<ItemStack> stack) {
        this.slot = slot;
        this.screen = screen;
        this.stack = stack.orElse(null);
    }

    @Override
    public void start(Context context) {
        MinionFakePlayer minion = context.getOrThrow(ExecutionContext.MINION_KEY);
        stack = SwapItemExecution.getStack(minion, slot, screen);
    }

    @Override
    public boolean isDone(Context context) {
        return true;
    }

    @Override
    public void stop(ParameterValueList list, Context context) {
        if(stack != null) {
            list.setValue(ID, BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
            list.setValue(COUNT, (long) stack.getCount());
        }
    }
}

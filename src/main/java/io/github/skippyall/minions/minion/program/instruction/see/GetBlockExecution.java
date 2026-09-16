package io.github.skippyall.minions.minion.program.instruction.see;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.minion.fakeplayer.Tracer;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.handler.Parameter;
import io.github.skippyall.minions.program.handler.ParameterValueList;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.registration.ExecutionContext;
import io.github.skippyall.minions.registration.ValueTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class GetBlockExecution implements InstructionExecution {
    public static final Codec<GetBlockExecution> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("fluids").forGetter(e -> e.fluids),
                    Codec.DOUBLE.fieldOf("reach").forGetter(e -> e.reach)
            ).apply(instance, GetBlockExecution::new)
    );

    public static final Parameter<Boolean> FLUIDS = new Parameter<>("fluids", ValueTypes.BOOLEAN);
    public static final Parameter<Double> REACH = new Parameter<>("reach", ValueTypes.DOUBLE);

    public static final Parameter<String> ID = new Parameter<>("id", ValueTypes.STRING);

    private boolean fluids;
    private double reach;

    public GetBlockExecution(ParameterValueList params, Context context) {
        this.fluids = params.getValue(FLUIDS);
        this.reach = params.getValue(REACH);
    }

    public GetBlockExecution(boolean fluids, double reach) {
        this.fluids = fluids;
        this.reach = reach;
    }

    @Override
    public boolean isDone(Context context) {
        return true;
    }

    @Override
    public void stop(ParameterValueList list, Context context) {
        MinionFakePlayer minion = context.getOrThrow(ExecutionContext.MINION_KEY);
        BlockHitResult hitResult = Tracer.rayTraceBlocks(minion, 0, reach, fluids);
        if(hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            BlockState state = minion.level().getBlockState(hitResult.getBlockPos());
            Identifier id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            list.setValue(ID, id.toString());
        }
    }
}

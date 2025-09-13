package io.github.skippyall.minions.program.instruction.execution;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.argument.ArgumentList;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.StringIdentifiable;

public class TurnExecution implements InstructionExecution<Void> {


    @Override
    public void start(MinionFakePlayer minion) {
        InstructionExecution.super.start(minion);
    }

    @Override
    public boolean isDone(MinionFakePlayer minion) {
        return false;
    }

    @Override
    public Void stop(MinionFakePlayer minion) {
        return null;
    }

    @Override
    public void readArguments(ArgumentList arguments, MinionFakePlayer minion) {

    }

    @Override
    public void save(WriteView view, MinionFakePlayer minion) {

    }

    @Override
    public void load(ReadView view, MinionFakePlayer minion) {

    }

    public enum TurnDirection implements StringIdentifiable {
        LEFT("left", -1, 0),
        UP("up", 0, -1),
        RIGHT("right", 1, 0),
        DOWN("down", 0, 1);

        public static final Codec<TurnDirection> CODEC = StringIdentifiable.createCodec(TurnDirection::values);

        public final String name;
        public final int xFactor;
        public final int yFactor;

        TurnDirection(String name, int xFactor, int yFactor) {
            this.xFactor = xFactor;
            this.yFactor = yFactor;
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }
    }
}

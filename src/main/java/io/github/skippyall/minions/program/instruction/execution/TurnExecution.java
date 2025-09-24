package io.github.skippyall.minions.program.instruction.execution;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.gui.Displayable;
import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.argument.ArgumentList;
import io.github.skippyall.minions.program.argument.Parameter;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.program.value.ValueTypes;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.StringIdentifiable;

import java.util.UUID;

public class TurnExecution implements InstructionExecution<Void> {
    public static final Parameter<Float> ANGLE = new Parameter<>("maxAngle", ValueTypes.FLOAT);
    public static final Parameter<TurnDirection> DIRECTION = new Parameter<>("direction", ValueTypes.TURN_DIRECTION);

    private static final float anglePerTick = 5;

    private float maxAngle;
    private float rotatedAngle;
    private TurnDirection direction;

    @Override
    public void tick(MinionFakePlayer minion) {
        float toRotate = Math.min(anglePerTick, maxAngle - rotatedAngle);
        minion.getMinionActionPack().turn(direction.xFactor * toRotate, direction.yFactor * toRotate);

        rotatedAngle += toRotate;
    }

    @Override
    public boolean isDone(MinionFakePlayer minion) {
        return Math.abs(maxAngle - rotatedAngle) < 0.001F;
    }

    @Override
    public Void stop(MinionFakePlayer minion) {
        return null;
    }

    @Override
    public void readArguments(ArgumentList arguments, MinionFakePlayer minion) {
        maxAngle = arguments.getValue(ANGLE, minion);
        direction = arguments.getValue(DIRECTION, minion);
    }

    @Override
    public void save(WriteView view, MinionFakePlayer minion) {
        view.putFloat("maxAngle", maxAngle);
        view.put("direction", TurnDirection.CODEC, direction);
    }

    @Override
    public void load(ReadView view, MinionFakePlayer minion) {
        maxAngle = view.getFloat("maxAngle", 0);
        direction = view.read("direction", TurnDirection.CODEC).orElseThrow();
    }

    public enum TurnDirection implements StringIdentifiable, Displayable {
        LEFT("left", -1, 0),
        UP("up", 0, -1),
        RIGHT("right", 1, 0),
        DOWN("down", 0, 1);

        public static final Codec<TurnDirection> CODEC = StringIdentifiable.createCodec(TurnDirection::values);

        private static final UUID MHF_ArrowLeft = UUID.fromString("a68f0b64-8d14-4000-a95f-4b9ba14f8df9");
        private static final UUID MHF_ArrowUp = UUID.fromString("fef039ef-e6cd-4987-9c84-26a3e6134277");
        private static final UUID MHF_ArrowRight = UUID.fromString("50c8510b-5ea0-4d60-be9a-7d542d6cd156");
        private static final UUID MHF_ArrowDown = UUID.fromString("68f59b9b-5b0b-4b05-a9f2-e1d1405aa348");

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

        @Override
        public GuiDisplay getDisplay() {
            return switch (this) {
                case LEFT -> new GuiDisplay.HeadBased(MHF_ArrowLeft, "minions.direction.left", false);
                case UP -> new GuiDisplay.HeadBased(MHF_ArrowUp, "minions.direction.up", false);
                case RIGHT -> new GuiDisplay.HeadBased(MHF_ArrowRight, "minions.direction.right", false);
                case DOWN -> new GuiDisplay.HeadBased(MHF_ArrowDown, "minions.direction.down", false);
            };
        }
    }
}

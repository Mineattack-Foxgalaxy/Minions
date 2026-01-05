package io.github.skippyall.minions.program.instruction.execution.move;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.gui.Displayable;
import io.github.skippyall.minions.gui.GuiDisplay;
import net.minecraft.util.StringIdentifiable;

import java.util.UUID;

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
            case LEFT -> new GuiDisplay.HeadBased(MHF_ArrowLeft);
            case UP -> new GuiDisplay.HeadBased(MHF_ArrowUp);
            case RIGHT -> new GuiDisplay.HeadBased(MHF_ArrowRight);
            case DOWN -> new GuiDisplay.HeadBased(MHF_ArrowDown);
        };
    }
}

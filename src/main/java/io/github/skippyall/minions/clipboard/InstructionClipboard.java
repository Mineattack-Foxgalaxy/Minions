package io.github.skippyall.minions.clipboard;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Uuids;

import java.util.UUID;
import java.util.function.Consumer;

public record InstructionClipboard(UUID selectedMinion, String selectedInstruction, String visualMinionName) implements Clipboard {
    public static final MapCodec<InstructionClipboard> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Uuids.CODEC.fieldOf("selectedMinion").forGetter(InstructionClipboard::selectedMinion),
                    Codec.STRING.fieldOf("selectedInstruction").forGetter(InstructionClipboard::selectedInstruction),
                    Codec.STRING.fieldOf("visualMinionName").forGetter(InstructionClipboard::visualMinionName)
            ).apply(instance, InstructionClipboard::new));

    @Override
    public MapCodec<? extends Clipboard> getCodec() {
        return CODEC;
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
        textConsumer.accept(Text.translatable("minions.reference.instruction.tooltip", selectedInstruction, visualMinionName));
    }
}

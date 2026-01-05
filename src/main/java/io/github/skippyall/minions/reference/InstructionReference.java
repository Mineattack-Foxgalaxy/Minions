package io.github.skippyall.minions.reference;

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

public record InstructionReference(UUID selectedMinion, String selectedInstruction, String visualMinionName) implements Reference {
    public static final MapCodec<InstructionReference> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Uuids.CODEC.fieldOf("selectedMinion").forGetter(InstructionReference::selectedMinion),
                    Codec.STRING.fieldOf("selectedInstruction").forGetter(InstructionReference::selectedInstruction),
                    Codec.STRING.fieldOf("visualMinionName").forGetter(InstructionReference::visualMinionName)
            ).apply(instance, InstructionReference::new));

    @Override
    public MapCodec<? extends Reference> getCodec() {
        return CODEC;
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
        textConsumer.accept(Text.translatable("minions.reference.instruction.tooltip", selectedInstruction, visualMinionName));
    }
}

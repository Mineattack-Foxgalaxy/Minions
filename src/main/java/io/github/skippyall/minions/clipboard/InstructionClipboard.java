package io.github.skippyall.minions.clipboard;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

public record InstructionClipboard(UUID selectedMinion, String selectedInstruction, String visualMinionName) implements Clipboard {
    public static final MapCodec<InstructionClipboard> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    UUIDUtil.AUTHLIB_CODEC.fieldOf("selectedMinion").forGetter(InstructionClipboard::selectedMinion),
                    Codec.STRING.fieldOf("selectedInstruction").forGetter(InstructionClipboard::selectedInstruction),
                    Codec.STRING.fieldOf("visualMinionName").forGetter(InstructionClipboard::visualMinionName)
            ).apply(instance, InstructionClipboard::new));

    @Override
    public MapCodec<? extends Clipboard> getCodec() {
        return CODEC;
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> textConsumer, TooltipFlag type, DataComponentGetter components) {
        textConsumer.accept(Component.translatable("minions.reference.instruction.tooltip", selectedInstruction, visualMinionName));
    }
}

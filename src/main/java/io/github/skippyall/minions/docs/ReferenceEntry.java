package io.github.skippyall.minions.docs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record ReferenceEntry(Text shortDescription, Text longDescription) {
    public static final Codec<ReferenceEntry> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    TextCodecs.CODEC.fieldOf("shortDescription").forGetter(ReferenceEntry::shortDescription),
                    TextCodecs.CODEC.fieldOf("longDescription").forGetter(ReferenceEntry::longDescription)
            ).apply(instance, ReferenceEntry::new));
}

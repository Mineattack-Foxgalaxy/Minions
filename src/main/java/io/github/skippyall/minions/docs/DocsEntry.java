package io.github.skippyall.minions.docs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.registration.MinionRegistries;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.dialog.body.DialogBody;

import java.util.List;
import java.util.function.Function;

public interface DocsEntry {
    Codec<DocsEntry> CODEC = MinionRegistries.DOCS_ENTRY_TYPES.byNameCodec().dispatch(DocsEntry::getCodec, Function.identity());

    Metadata getMetadata();

    List<DialogBody> getDialog(RegistryAccess manager);

    MapCodec<? extends DocsEntry> getCodec();

    record Metadata(String titleKey) {
        public static final MapCodec<Metadata> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.STRING.fieldOf("title").forGetter(Metadata::titleKey)
                ).apply(instance, Metadata::new)
        );
    }
}

package io.github.skippyall.minions.docs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.registration.MinionRegistries;
import net.minecraft.dialog.body.DialogBody;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.function.Function;

public interface DocsEntry {
    Codec<DocsEntry> CODEC = MinionRegistries.DOCS_ENTRY_TYPES.getCodec().dispatch(DocsEntry::getCodec, Function.identity());

    Metadata getMetadata();

    List<DialogBody> getDialog(DynamicRegistryManager manager);

    MapCodec<? extends DocsEntry> getCodec();

    record Metadata(String titleKey) {
        public static final MapCodec<Metadata> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.STRING.fieldOf("title").forGetter(Metadata::titleKey)
                ).apply(instance, Metadata::new)
        );
    }
}

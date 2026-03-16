package io.github.skippyall.minions.docs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.gui.GuiDisplay;
import net.minecraft.dialog.body.DialogBody;
import net.minecraft.dialog.body.ItemDialogBody;
import net.minecraft.dialog.body.PlainMessageDialogBody;
import net.minecraft.item.Item;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record ReferenceEntry(Metadata metadata, RegistryKey<?> object, Text shortDescription, Text longDescription) implements DocsEntry {
    private static final Codec<RegistryKey<?>> REGISTRY_KEY_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("registry").forGetter(RegistryKey::getRegistry),
                    Identifier.CODEC.fieldOf("value").forGetter(RegistryKey::getValue)
            ).apply(instance, (registry, value) -> RegistryKey.of(RegistryKey.ofRegistry(registry), value))
    );

    public static final MapCodec<ReferenceEntry> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Metadata.CODEC.fieldOf("metadata").forGetter(ReferenceEntry::getMetadata),
                    REGISTRY_KEY_CODEC.fieldOf("object").forGetter(ReferenceEntry::object),
                    TextCodecs.CODEC.fieldOf("shortDescription").forGetter(ReferenceEntry::shortDescription),
                    TextCodecs.CODEC.fieldOf("longDescription").forGetter(ReferenceEntry::longDescription)
            ).apply(instance, ReferenceEntry::new));

    @Override
    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public List<DialogBody> getDialog(DynamicRegistryManager manager) {
        List<DialogBody> bodyElements = new ArrayList<>();

        GuiDisplay display = getObjectDisplay(manager);
        if(display != null) {
            bodyElements.add(new ItemDialogBody(display.createItemStack(), Optional.empty(), false, false, 16, 16));
        }
        bodyElements.add(new PlainMessageDialogBody(Text.translatable(object.getValue().toTranslationKey(object.getRegistry().getPath())), 200));
        bodyElements.add(new PlainMessageDialogBody(longDescription.copy().append("\n".repeat(100)), 200));

        return bodyElements;
    }

    public GuiDisplay getObjectDisplay(DynamicRegistryManager manager) {
        GuiDisplay display = GuiDisplay.DEFAULT_DISPLAY;
        if(object.isOf(RegistryKeys.ITEM) || object.isOf(RegistryKeys.BLOCK)) {
            Item item;
            if(object.isOf(RegistryKeys.ITEM)) {
                item = Registries.ITEM.get(object.getValue());
            } else {
                item = Registries.BLOCK.get(object.getValue()).asItem();
            }
            if(item != null) {
                display = new GuiDisplay.ItemBased(item);
            }
        } else {
            Identifier displayId = object.getValue().withPrefixedPath(object.getRegistry().getPath() + "/");
            display = GuiDisplay.getGuiDisplay(displayId, manager);
        }
        return display;
    }

    @Override
    public MapCodec<? extends DocsEntry> getCodec() {
        return CODEC;
    }
}

package io.github.skippyall.minions.docs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.gui.GuiDisplay;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dialog.body.DialogBody;
import net.minecraft.server.dialog.body.ItemBody;
import net.minecraft.server.dialog.body.PlainMessage;
import net.minecraft.world.item.Item;

public record ReferenceEntry(Metadata metadata, ResourceKey<?> object, Component shortDescription, Component longDescription) implements DocsEntry {
    private static final Codec<ResourceKey<?>> REGISTRY_KEY_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("registry").forGetter(ResourceKey::registry),
                    ResourceLocation.CODEC.fieldOf("value").forGetter(ResourceKey::location)
            ).apply(instance, (registry, value) -> ResourceKey.create(ResourceKey.createRegistryKey(registry), value))
    );

    public static final MapCodec<ReferenceEntry> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Metadata.CODEC.fieldOf("metadata").forGetter(ReferenceEntry::getMetadata),
                    REGISTRY_KEY_CODEC.fieldOf("object").forGetter(ReferenceEntry::object),
                    ComponentSerialization.CODEC.fieldOf("shortDescription").forGetter(ReferenceEntry::shortDescription),
                    ComponentSerialization.CODEC.fieldOf("longDescription").forGetter(ReferenceEntry::longDescription)
            ).apply(instance, ReferenceEntry::new));

    @Override
    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public List<DialogBody> getDialog(RegistryAccess manager) {
        List<DialogBody> bodyElements = new ArrayList<>();

        GuiDisplay display = getObjectDisplay(manager);
        if(display != null) {
            bodyElements.add(new ItemBody(display.createItemStack(), Optional.empty(), false, false, 16, 16));
        }
        bodyElements.add(new PlainMessage(Component.translatable(object.location().toLanguageKey(object.registry().getPath())), 200));
        bodyElements.add(new PlainMessage(longDescription, 200));

        return bodyElements;
    }

    public GuiDisplay getObjectDisplay(RegistryAccess manager) {
        GuiDisplay display = GuiDisplay.DEFAULT_DISPLAY;
        if(object.isFor(Registries.ITEM) || object.isFor(Registries.BLOCK)) {
            Item item;
            if(object.isFor(Registries.ITEM)) {
                item = BuiltInRegistries.ITEM.getValue(object.location());
            } else {
                item = BuiltInRegistries.BLOCK.getValue(object.location()).asItem();
            }
            if(item != null) {
                display = new GuiDisplay.ItemBased(item);
            }
        } else {
            ResourceLocation displayId = object.location().withPrefix(object.registry().getPath() + "/");
            display = GuiDisplay.getGuiDisplay(displayId, manager);
        }
        return display;
    }

    @Override
    public MapCodec<? extends DocsEntry> getCodec() {
        return CODEC;
    }
}

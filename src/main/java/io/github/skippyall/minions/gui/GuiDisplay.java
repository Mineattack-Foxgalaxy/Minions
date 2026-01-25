package io.github.skippyall.minions.gui;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.Codec;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.util.TranslationUtil;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSets;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Uuids;

import java.util.Optional;
import java.util.UUID;

public interface GuiDisplay {
    Codec<GuiDisplay> CODEC = MinionRegistries.GUI_DISPLAY_TYPE.getCodec().dispatch(GuiDisplay::getCodec, codec -> codec.fieldOf("data"));
    GuiDisplay DEFAULT_DISPLAY = new ItemBased(Items.BARRIER);

    static GuiDisplay getGuiDisplay(Identifier id, DynamicRegistryManager manager) {
        return manager.getOptional(MinionRegistries.GUI_DISPLAY).map(registry -> registry.get(id)).orElse(DEFAULT_DISPLAY);
    }

    static <T> GuiDisplay getGuiDisplayFor(Registry<T> registry, T element, DynamicRegistryManager manager) {
        Identifier elementId = registry.getId(element);
        if(elementId == null) {
            return DEFAULT_DISPLAY;
        }
        Identifier displayId = elementId.withPrefixedPath(registry.getKey().getValue().getPath() + "/");

        return getGuiDisplay(displayId, manager);
    }

    static <T> ItemStack getDisplayStack(Registry<T> registry, T element, DynamicRegistryManager manager) {
        return getGuiDisplayFor(registry, element, manager).createItemStack();
    }

    static <T> ItemStack getDisplayStackWithName(Registry<T> registry, T element, DynamicRegistryManager manager) {
        ItemStack stack = getDisplayStack(registry, element, manager);
        stack.set(DataComponentTypes.ITEM_NAME, Text.translatable(TranslationUtil.getTranslationKey(element, registry)));
        return stack;
    }

    ItemStack createItemStack();

    Codec<? extends GuiDisplay> getCodec();

    class ModelBased implements GuiDisplay {
        public static final Codec<ModelBased> CODEC = Identifier.CODEC.xmap(ModelBased::new, display -> display.model);

        private final Identifier model;

        public ModelBased(Identifier model) {
            this.model = model;
        }

        @Override
        public ItemStack createItemStack() {
            ItemStack stack = new ItemStack(Items.BARRIER);
            stack.set(DataComponentTypes.ITEM_MODEL, model);
            return stack;
        }

        @Override
        public Codec<? extends GuiDisplay> getCodec() {
            return CODEC;
        }
    }

    class ItemBased implements GuiDisplay {
        public static final Codec<ItemBased> CODEC = Registries.ITEM.getCodec().xmap(ItemBased::new, display -> display.item);

        private final Item item;

        public ItemBased(Item item) {
            this.item = item;
        }

        @Override
        public ItemStack createItemStack() {
            ItemStack stack = new ItemStack(item);
            stack.set(DataComponentTypes.TOOLTIP_DISPLAY, new TooltipDisplayComponent(true, ReferenceSortedSets.emptySet()));
            stack.set(DataComponentTypes.RARITY, Rarity.COMMON);
            return stack;
        }

        @Override
        public Codec<? extends GuiDisplay> getCodec() {
            return CODEC;
        }
    }

    class HeadBased implements GuiDisplay {
        public static final Codec<HeadBased> CODEC = Uuids.CODEC.xmap(HeadBased::new, display -> display.uuid);

        private final UUID uuid;

        public HeadBased(UUID uuid) {
            this.uuid = uuid;
        }

        @Override
        public ItemStack createItemStack() {
            ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
            stack.set(DataComponentTypes.PROFILE, new ProfileComponent(Optional.empty(), Optional.of(uuid), new PropertyMap()));
            return stack;
        }

        @Override
        public Codec<? extends GuiDisplay> getCodec() {
            return CODEC;
        }
    }

    class StackBased implements GuiDisplay {
        public static final Codec<StackBased> CODEC = ItemStack.CODEC.xmap(StackBased::new, StackBased::createItemStack);

        private final ItemStack stack;

        public StackBased(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public ItemStack createItemStack() {
            return stack;
        }

        @Override
        public Codec<? extends GuiDisplay> getCodec() {
            return CODEC;
        }
    }

    static void register() {
        Registry.register(MinionRegistries.GUI_DISPLAY_TYPE, Identifier.of(Minions.MOD_ID, "item"), ItemBased.CODEC);
        Registry.register(MinionRegistries.GUI_DISPLAY_TYPE, Identifier.of(Minions.MOD_ID, "model"), ModelBased.CODEC);
        Registry.register(MinionRegistries.GUI_DISPLAY_TYPE, Identifier.of(Minions.MOD_ID, "head"), HeadBased.CODEC);
        Registry.register(MinionRegistries.GUI_DISPLAY_TYPE, Identifier.of(Minions.MOD_ID, "stack"), StackBased.CODEC);
    }
}

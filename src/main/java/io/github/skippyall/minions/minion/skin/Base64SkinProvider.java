package io.github.skippyall.minions.minion.skin;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import io.github.skippyall.minions.Minions;
import net.minecraft.dialog.AfterAction;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.dialog.action.DynamicCustomDialogAction;
import net.minecraft.dialog.input.TextInputControl;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.dialog.type.DialogInput;
import net.minecraft.dialog.type.NoticeDialog;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Base64SkinProvider implements SkinProvider {
    public static final RegistryKey<Dialog> DIALOG = RegistryKey.of(RegistryKeys.DIALOG, Identifier.of(Minions.MOD_ID, "base_64_input"));
    public static final Identifier CUSTOM_DIALOG_ACTION = Identifier.of(Minions.MOD_ID, "base_64_submit");

    private static long dialogIdCounter = 0;
    private static Map<Long, CompletableFuture<Optional<PropertyMap>>> futures = new HashMap<>();

    @Override
    public CompletableFuture<Optional<PropertyMap>> openSkinMenu(ServerPlayerEntity player) {
        dialogIdCounter++;
        player.openDialog(getDialog());
        CompletableFuture<Optional<PropertyMap>> future = new CompletableFuture<>();
        futures.put(dialogIdCounter, future);
        return future;
    }

    public static void onCustomDialogAction(Optional<NbtElement> element) {
        if(element.isPresent() && element.get() instanceof NbtCompound compound) {
            Optional<Long> id = compound.getLong("dialog_id");
            Optional<String> base64 = compound.getString("base_64");
            if(id.isPresent() && base64.isPresent() && !base64.get().isBlank()) {
                if(futures.containsKey(id.get())) {
                    PropertyMap map = new PropertyMap();
                    map.put("textures", new Property("textures", base64.get().strip()));

                    futures.get(id.get()).complete(Optional.of(map));
                    futures.remove(id.get());
                }
            }
        }
    }

    private static RegistryEntry<Dialog> getDialog() {
        NbtCompound additionalData = new NbtCompound();
        additionalData.putLong("dialog_id", dialogIdCounter);
        return RegistryEntry.of(
                new NoticeDialog(
                        new DialogCommonData(
                                Text.translatable("minions.gui.look.skin.base64.title"),
                                Optional.empty(),
                                true,
                                false,
                                AfterAction.CLOSE,
                                List.of(),
                                List.of(
                                        new DialogInput("base_64", new TextInputControl(
                                                200,
                                                Text.empty(),
                                                false,
                                                "",
                                                2000,
                                                Optional.empty()
                                        ))
                                )
                        ),
                        new DialogActionButtonData(
                                new DialogButtonData(
                                        Text.translatable("gui.ok"),
                                        150
                                ),
                                Optional.of(
                                        new DynamicCustomDialogAction(
                                                CUSTOM_DIALOG_ACTION,
                                                Optional.of(
                                                        additionalData
                                                )
                                        )
                                )
                        )
                )
        );
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("minions.gui.look.skin.base64");
    }
}

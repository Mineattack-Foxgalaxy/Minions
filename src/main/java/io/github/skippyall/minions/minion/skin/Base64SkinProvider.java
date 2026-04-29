package io.github.skippyall.minions.minion.skin;

import com.google.common.collect.ImmutableMultimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import io.github.skippyall.minions.Minions;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.CommonDialogData;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.DialogAction;
import net.minecraft.server.dialog.Input;
import net.minecraft.server.dialog.NoticeDialog;
import net.minecraft.server.dialog.action.CustomAll;
import net.minecraft.server.dialog.input.TextInput;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Base64SkinProvider implements SkinProvider {
    public static final ResourceKey<Dialog> DIALOG = ResourceKey.create(Registries.DIALOG, Identifier.fromNamespaceAndPath(Minions.MOD_ID, "base_64_input"));
    public static final Identifier CUSTOM_DIALOG_ACTION = Identifier.fromNamespaceAndPath(Minions.MOD_ID, "base_64_submit");

    private static long dialogIdCounter = 0;
    private static Map<Long, CompletableFuture<ResolvableProfile>> futures = new HashMap<>();

    @Override
    public CompletableFuture<ResolvableProfile> openSkinMenu(ServerPlayer player) {
        dialogIdCounter++;
        player.openDialog(getDialog());
        CompletableFuture<ResolvableProfile> future = new CompletableFuture<>();
        futures.put(dialogIdCounter, future);
        return future;
    }

    public static void onCustomDialogAction(Optional<Tag> element) {
        if(element.isPresent() && element.get() instanceof CompoundTag compound) {
            Optional<Long> id = compound.getLong("dialog_id");
            Optional<String> base64 = compound.getString("base_64");
            if(id.isPresent() && base64.isPresent() && !base64.get().isBlank()) {
                if(futures.containsKey(id.get())) {
                    PropertyMap map = new PropertyMap(ImmutableMultimap.of(
                            "textures", new Property("textures", base64.get().strip())
                    ));

                    futures.get(id.get()).complete(ResolvableProfile.createResolved(new GameProfile(FakePlayer.DEFAULT_UUID, "", map)));
                    futures.remove(id.get());
                }
            }
        }
    }

    private static Holder<Dialog> getDialog() {
        CompoundTag additionalData = new CompoundTag();
        additionalData.putLong("dialog_id", dialogIdCounter);
        return Holder.direct(
                new NoticeDialog(
                        new CommonDialogData(
                                Component.translatable("minions.gui.look.skin.base64.title"),
                                Optional.empty(),
                                true,
                                false,
                                DialogAction.CLOSE,
                                List.of(),
                                List.of(
                                        new Input("base_64", new TextInput(
                                                200,
                                                Component.empty(),
                                                false,
                                                "",
                                                2000,
                                                Optional.empty()
                                        ))
                                )
                        ),
                        new ActionButton(
                                new CommonButtonData(
                                        Component.translatable("gui.ok"),
                                        150
                                ),
                                Optional.of(
                                        new CustomAll(
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
    public Component getDisplayName() {
        return Component.translatable("minions.gui.look.skin.base64");
    }
}

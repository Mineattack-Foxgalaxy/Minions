package io.github.skippyall.minions.minion;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.ProfileResult;
import io.github.skippyall.minions.input.Result;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

import static io.github.skippyall.minions.Minions.LOGGER;

public class MinionProfileUtils {
    public static final String PREFIX = "#";

    public static CompletableFuture<@Nullable GameProfile> lookupSkinOwnerProfile(MinecraftServer server, String username) {
        CompletableFuture<GameProfile> future = new CompletableFuture<>();

        ForkJoinPool.commonPool().execute(() -> {
            try {
                server.getGameProfileRepo().findProfilesByNames(new String[]{username}, new ProfileLookupCallback() {
                    @Override
                    public void onProfileLookupSucceeded(GameProfile found) {
                        LOGGER.info("SkinProfile: {}", found);
                        try {
                            getSkinOwnerProfile(server, found.getId()).thenAccept(future::complete);
                        } catch (Throwable ex) {
                            LOGGER.warn("Exception during Game Profile creation", ex);
                        }
                    }

                    @Override
                    public void onProfileLookupFailed(String profileName, Exception exception) {
                        LOGGER.warn("Lookup Error: ", exception);
                        future.complete(null);
                    }
                });
            } catch (Throwable e) {
                LOGGER.warn("Failed to get UUID for username " + username, e);
                future.complete(null);
            }
        });

        return future;
    }

    public static CompletableFuture<@Nullable GameProfile> getSkinOwnerProfile(MinecraftServer server, @Nullable UUID uuid) {
        CompletableFuture<GameProfile> future = new CompletableFuture<>();
        future.completeAsync(() -> {
            GameProfile profile = null;
            if(uuid != null) {
                ProfileResult result = server.getSessionService().fetchProfile(uuid, true);
                if (result != null) {
                    profile = result.profile();
                    LOGGER.info("Full SkinProfile: {}", profile);
                }
            }
            return profile;
        });
        return future;
    }

    public static GameProfile makeNewMinionProfile(UUID uuidMinion, String username, PropertyMap skin) {
        if(uuidMinion == null) {
            uuidMinion = UUID.randomUUID();
        }

        GameProfile newProfile = new GameProfile(uuidMinion, username);
        if (skin != null) {
            newProfile.getProperties().putAll(skin);
        }
        LOGGER.info("Minion Profile: {}", newProfile);
        return newProfile;
    }

    public static Result<String, Text> checkMinionName(String name) {
        if(StringHelper.isValidPlayerName(PREFIX + name)) {
            return new Result.Success<>(name);
        } else {
            return new Result.Error<>(Text.translatable("minions.generic.minion_name_too_long"));
        }
    }

    public static boolean isValidMinionName(String name) {
        return checkMinionName(name).isSuccess();
    }

    public static boolean isMinion(UUID uuid) {
        return MinionPersistentState.INSTANCE.isMinion(uuid);
    }
}

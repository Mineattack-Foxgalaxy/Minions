package io.github.skippyall.minions.minion;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.yggdrasil.ProfileResult;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

import static io.github.skippyall.minions.Minions.LOGGER;

public class MinionProfileUtils {
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

    public static GameProfile makeNewMinionProfile(@Nullable UUID uuidMinion, String username, @Nullable GameProfile skinProfile) {
        GameProfile newProfile = new GameProfile(uuidMinion != null ? uuidMinion : UUID.randomUUID(), username);
        if (skinProfile != null) {
            newProfile.getProperties().putAll(skinProfile.getProperties());
        }
        LOGGER.info("Minion Profile: {}", newProfile);
        return newProfile;
    }
}

package io.github.skippyall.minions.minion;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.brigadier.StringReader;
import io.github.skippyall.minions.gui.input.Result;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;

import java.util.UUID;

import static io.github.skippyall.minions.Minions.LOGGER;

public class MinionProfileUtils {
    public static final String PREFIX = "+";

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

    public static Result<String, Text> checkMinionNameWithoutPrefix(String name) {
        for(char c : name.toCharArray()) {
            if(!StringReader.isAllowedInUnquotedString(c)) {
                return new Result.Error<>(Text.translatable("minions.generic.name.invalid_char"));
            }
        }

        if((PREFIX + name).length() > 16)  {
            return new Result.Error<>(Text.translatable("minions.generic.name.too_long"));
        }

        if(!StringHelper.isValidPlayerName(PREFIX + name)) {
            return new Result.Error<>(Text.translatable("minions.generic.name.invalid"));
        }

        if(MinionPersistentState.INSTANCE.isMinionNameTaken(PREFIX + name)) {
            return new Result.Error<>(Text.translatable("minions.generic.name.taken"));
        }

        return new Result.Success<>(name);
    }

    public static String newDefaultMinionName() {
        int i = 0;
        while (MinionPersistentState.INSTANCE.isMinionNameTaken("+Minion" + i)) {
            i++;
        }
        return "+Minion" + i;
    }

    public static boolean isMinion(UUID uuid) {
        return MinionPersistentState.INSTANCE.isMinion(uuid);
    }
}

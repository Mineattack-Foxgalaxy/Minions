package io.github.skippyall.minions;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.core.serde.ObjectDeserializer;
import com.electronwill.nightconfig.core.serde.ObjectSerializer;
import com.electronwill.nightconfig.core.serde.SerdeException;
import com.electronwill.nightconfig.core.serde.annotations.SerdeComment;
import com.electronwill.nightconfig.core.serde.annotations.SerdeSkipDeserializingIf;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.toml.TomlParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.electronwill.nightconfig.core.serde.annotations.SerdeSkipDeserializingIf.SkipDeIf.IS_MISSING;

public class MinionsConfig {
    private static MinionsConfig INSTANCE;

    @SerdeSkipDeserializingIf(IS_MISSING)
    public Minion minion = new Minion();

    public static class Minion {
        @SerdeComment("The prefix for all minion names")
        @SerdeSkipDeserializingIf(IS_MISSING)
        public String minionPrefix = "+";

        @SerdeComment("Makes minions not raise the mob cap if they can't spawn mobs.")
        @SerdeComment("Might cause incompatibilities.")
        @SerdeSkipDeserializingIf(IS_MISSING)
        public boolean enableMobCapHacks = true;
    }

    @SerdeSkipDeserializingIf(IS_MISSING)
    public Compat compat = new Compat();

    public static class Compat {
        @SerdeComment("Enables compat with Universal Graves, which allows everyone to pick up graves from minions")
        @SerdeSkipDeserializingIf(IS_MISSING)
        public boolean enableGravesCompat = true;
    }

    private static Path getPath() {
        Path minionsDir = FabricLoader.getInstance().getConfigDir().resolve("minions");
        if(!Files.isDirectory(minionsDir)) {
            try {
                Files.createDirectory(minionsDir);
            } catch (IOException e) {
                Minions.LOGGER.error("Could not create config dir", e);
            }
        }
        return minionsDir.resolve(Minions.MOD_ID + ".toml");
    }

    public static MinionsConfig get() {
        if(INSTANCE == null) {
            loadConfig();
        }
        return INSTANCE;
    }

    public static void loadConfig() {
        try {
            CommentedConfig config = new TomlParser().parse(getPath(), (file, configFormat) -> {
                CommentedConfig defaultConfig = ObjectSerializer.standard().serializeFields(new MinionsConfig(), TomlFormat::newConfig);
                configFormat.createWriter().write(defaultConfig, file, WritingMode.REPLACE);
                return true;
            });

            INSTANCE = ObjectDeserializer.standard().deserializeFields(config, MinionsConfig::new);
        } catch (SerdeException | ParsingException | WritingException e) {
            Minions.LOGGER.error("Error while reading config", e);
            INSTANCE = new MinionsConfig();
        }
    }

    /*public static void saveConfig() {
        try {
            CommentedConfig config = ObjectSerializer.standard().serializeFields(INSTANCE, TomlFormat::newConfig);
            new TomlWriter().write(config, getPath(), WritingMode.REPLACE);
        } catch (SerdeException | ParsingException | WritingException e) {
            System.out.println("[minions] Error while writing config");
            e.printStackTrace();
        }
    }*/
}

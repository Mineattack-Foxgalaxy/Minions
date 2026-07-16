package io.github.skippyall.minions;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.core.serde.ObjectDeserializer;
import com.electronwill.nightconfig.core.serde.ObjectSerializer;
import com.electronwill.nightconfig.core.serde.SerdeException;
import com.electronwill.nightconfig.core.serde.annotations.SerdeComment;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.toml.TomlParser;
import net.fabricmc.loader.api.FabricLoader;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MinionsConfig {
    private static @Nullable MinionsConfig INSTANCE;

    public Minion minion = new Minion();

    public static class Minion {
        @SerdeComment("The prefix for all minion names")
        public String minionPrefix = "+";

        @SerdeComment("Makes minions not raise the mob cap if they can't spawn mobs.")
        @SerdeComment("Might cause incompatibilities.")
        public boolean enableMobCapModification = true;
    }

    public Compat compat = new Compat();

    public static class Compat {
        @SerdeComment("Enables compat with Universal Graves, which allows everyone to pick up graves from minions")
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
            CommentedConfig defaultConfig = ObjectSerializer.standard().serializeFields(new MinionsConfig(), TomlFormat::newConfig);

            CommentedConfig config = new TomlParser().parse(getPath(), (file, configFormat) -> {
                configFormat.createWriter().write(defaultConfig, file, WritingMode.REPLACE);
                return true;
            });

            //Always use default values when entries are missing
            config.addAll(defaultConfig);
            INSTANCE = ObjectDeserializer.standard().deserializeFields(config, MinionsConfig::new);
        } catch (SerdeException | ParsingException | WritingException e) {
            Minions.LOGGER.error("Error while reading config", e);
            INSTANCE = new MinionsConfig();
        }
    }
}

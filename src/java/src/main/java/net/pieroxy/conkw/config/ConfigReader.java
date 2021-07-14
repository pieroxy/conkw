package net.pieroxy.conkw.config;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.runtime.Settings;
import net.pieroxy.conkw.utils.RemoveJsonCommentsInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ConfigReader {
    public static final String NAME = "config.jsonc";
    public static final String LOGGING_NAME = "logging.properties";

    private static File home;

    public static Config getConfig() {
        Config config = null;
        try (InputStream is = new FileInputStream(getConfigFile())) {
            config =  new DslJson<>().deserialize(Config.class, new RemoveJsonCommentsInputStream(is, getConfigFile().getAbsolutePath()));
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse config file " + getConfigFile().getAbsolutePath() + ": " + e.getMessage(), e);
        }

        return config;
    }

    public static File getHomeDir() {
        if (home == null) {
            String homes = System.getenv("CONKW_HOME");
            if (homes == null) {
                home = new File(System.getProperty("user.home"), ".conkw");
            } else {
                home = new File(homes);
            }
        }
        return home;
    }

    public static File getDataDir() {
        return new File(getHomeDir(), "data");
    }

    public static File getConfDir() {
        return new File(getHomeDir(), "config");
    }

    public static File getConfigFile() {
        return new File(getConfDir(), NAME);
    }

    public static File getLoggingConfigFile() {
        return new File(getConfDir(), LOGGING_NAME);
    }

    public static File getTmpDir() {
        return new File(getHomeDir(), "tmp");
    }

    public static File getBinDir() {
        return new File(getHomeDir(), "bin");
    }

    public static File getLogDir() {
        return new File(getHomeDir(), "log");
    }

    public static File getWebappDir() {
        return new File(getHomeDir(), "webapp/ROOT");
    }

    public static File getUiDir() {
        return new File(getHomeDir(), "ui");
    }

    public static boolean exists() {
        return getHomeDir().exists() && getHomeDir().isDirectory() &&
                getDataDir().exists() && getDataDir().isDirectory() &&
                getConfDir().exists() && getConfDir().isDirectory() &&
                getTmpDir().exists() && getTmpDir().isDirectory() &&
                getLogDir().exists() && getLogDir().isDirectory() &&
                getBinDir().exists() && getBinDir().isDirectory() &&
                getWebappDir().exists() && getWebappDir().isDirectory() &&
                getUiDir().exists() && getUiDir().isDirectory();
    }

    public static void defineHomeDirectory(String home) {
        ConfigReader.home = new File(home);
    }
}

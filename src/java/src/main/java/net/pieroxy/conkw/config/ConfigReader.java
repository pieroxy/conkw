package net.pieroxy.conkw.config;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.runtime.Settings;
import net.pieroxy.conkw.grabbersBase.Grabber;
import net.pieroxy.conkw.utils.RemoveJsonCommentsInputStream;
import net.pieroxy.conkw.utils.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ConfigReader {
    private final static Logger LOGGER = Logger.getLogger(ConfigReader.class.getName());
    public static final String NAME = "config.jsonc";
    public static final String CREDS_NAME = "credentials.jsonc";
    public static final String LOGGING_NAME = "logging.properties";

    private static Map<String,String> defaultNames = new HashMap<>();

    private static File home;

    public static Config getConfig() {
        Config config = null;
        if (!getConfigFile().exists()) { // Probably being installed.
            Config c = new Config();
            c.setHttpPort(-1);
            return c;
        }
        try (InputStream is = new FileInputStream(getConfigFile())) {
            config =  new DslJson<>(Settings.basicSetup()).deserialize(Config.class, new RemoveJsonCommentsInputStream(is, getConfigFile().getAbsolutePath()));
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse config file " + getConfigFile().getAbsolutePath() + ": " + e.getMessage(), e);
        }

        Arrays.stream(config.getGrabbers()).forEach(c -> {
            if (StringUtil.isNullOrEmptyTrimmed(c.getName())) {
                c.setName(getDefaultName(c.getImplementation()));
            }
        });

        return config;
    }

    public static String getDefaultName(String grabberImplementation) {
        String res = defaultNames.get(grabberImplementation);
        if (res == null) {
            Map<String,String> ndn = new HashMap<>(defaultNames);
            try {
                ndn.put(grabberImplementation, res = ((Grabber)Class.forName(grabberImplementation).newInstance()).getDefaultName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            defaultNames = ndn;
        }
        return res;
    }

    public static CredentialsStore getCredentials() {
        File file = getCredentialsFile();
        if (!file.exists()) {
            LOGGER.warning("Credentials file not found at " + file.getAbsolutePath());
            CredentialsStore c = new CredentialsStore();
            c.setStore(new HashMap<>());
            return c;
        }
        LOGGER.info("Reading credentials file at " + file.getAbsolutePath());
        CredentialsStore cs = null;
        try (InputStream is = new FileInputStream(file)) {
            cs =  new DslJson<>(Settings.basicSetup()).deserialize(CredentialsStore.class, new RemoveJsonCommentsInputStream(is, getConfigFile().getAbsolutePath()));
            for (String s : cs.getStore().keySet()) {
                cs.getStore().get(s).setReference(s);
            }
        } catch (Exception e) {
            LOGGER.severe("Unparseable credentials file " + file.getAbsolutePath());
            throw new RuntimeException("Unable to parse credentials file file " + file.getAbsolutePath() + ": " + e.getMessage(), e);
        }

        return cs;
    }

    public static File getCredentialsFile() {
        return new File(getConfDir(), CREDS_NAME);
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

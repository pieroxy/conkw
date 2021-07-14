package net.pieroxy.conkw.standalone;

import net.pieroxy.conkw.config.ConfigReader;
import net.pieroxy.conkw.utils.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Installer {

    private final boolean overrideConfig;
    private final boolean overrideUI;

    public Installer(boolean overrideConfig, boolean overrideUI) {
        this.overrideConfig = overrideConfig;
        this.overrideUI = overrideUI;
    }

    public void run() throws Exception {
        System.out.println("** INSTALLING CONKW **");
        System.out.println("Installing conkw in " + ConfigReader.getHomeDir());
        doInstall();
        printInstructions();
    }

    private void printInstructions() {
        switch (OsCheck.getOperatingSystemType()) {
            case Windows:
                System.out.println("Congratulations. You can now run conkw by typing:");
                System.out.println("C:\\> " + ConfigReader.getBinDir() + File.separator + getFilename());
                break;
            default:
                System.out.println("Congratulations. You can now run conkw by typing:");
                System.out.println("$ sh " + ConfigReader.getBinDir() + File.separator + getFilename());
                System.out.println("");
                System.out.println("If you want to have this program automatically launched at startup, just add the following line to your crontab:");
                System.out.println("@reboot sh " + ConfigReader.getBinDir() + File.separator + getFilename());
                break;
        }
    }

    private void doInstall() throws Exception {
        String java = System.getProperty("java.home");

        // Creating directory structure
        Files.createDirectories(ConfigReader.getConfDir().toPath());
        Files.createDirectories(ConfigReader.getDataDir().toPath());
        Files.createDirectories(ConfigReader.getTmpDir().toPath());
        Files.createDirectories(ConfigReader.getBinDir().toPath());
        Files.createDirectories(ConfigReader.getLogDir().toPath());
        Files.createDirectories(ConfigReader.getUiDir().toPath());
        Files.createDirectories(ConfigReader.getWebappDir().toPath());

        // Initializing config directory
        File sampleFG = new File(ConfigReader.getConfDir(), "example.properties");
        StreamTools.copyTextFilePreserveOriginalAndWarnOnStdout(
            getClass().getClassLoader().getResourceAsStream("config.sample.jsonc"),
            ConfigReader.getConfigFile(),
            overrideConfig,
            new TextReplacer()
                .add("$FGSF", sampleFG.getAbsolutePath()));
        StreamTools.copyTextFilePreserveOriginalAndWarnOnStdout(
            getClass().getClassLoader().getResourceAsStream("example.properties"),
            sampleFG,overrideConfig,new TextReplacer().add("$FGSF", sampleFG.getAbsolutePath()));
        StreamTools.copyTextFilePreserveOriginalAndWarnOnStdout(
                getClass().getClassLoader().getResourceAsStream("logging.properties"),
                ConfigReader.getLoggingConfigFile(),
                overrideConfig, null);

        // Deploying webapp
        initWebapp();

        // Deploying binaries and startup script.
        Files.copy(
                new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).toPath(),
                new File(ConfigReader.getBinDir(), "conkw.jar").toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        FileOutputStream script = new FileOutputStream(new File(ConfigReader.getBinDir(), getFilename()));
        script.write(("\"" + java + "/bin/java\" -verbose:gc \"-Djava.util.logging.config.file="+ConfigReader.getLoggingConfigFile().getAbsolutePath()+"\" -Xms50m -jar \"" + ConfigReader.getBinDir() + File.separator + "conkw.jar\" start --stopCurrentInstance --home "+ConfigReader.getHomeDir()+" >> \"" + ConfigReader.getLogDir() + "/system.log\" 2>&1\n").getBytes());
        script.close();

        // Install default UI
        switch (OsCheck.getOperatingSystemType()) {
            case Linux:
                StreamTools.copyTextFilePreserveOriginalAndWarnOnStdout(
                    getClass().getClassLoader().getResourceAsStream("default-linux.html"),
                    new File(ConfigReader.getUiDir(), "index.html"),
                    overrideUI, null);
                break;
            default:
                StreamTools.copyTextFilePreserveOriginalAndWarnOnStdout(
                    getClass().getClassLoader().getResourceAsStream("default-nonlinux.html"),
                    new File(ConfigReader.getUiDir(), "index.html"),
                    overrideUI, null);
                break;
        }
    }

    private String getFilename() {
        switch (OsCheck.getOperatingSystemType()) {
            case Windows:
                return "run.cmd";
            default:
                return "run.sh";
        }
    }

    private void initWebapp() throws IOException {
        File h = ConfigReader.getWebappDir();
        File webinf = new File(h, "WEB-INF");
        Files.createDirectories(webinf.toPath());
        ZipUtil.unzip(getClass().getClassLoader().getResourceAsStream("webapp-static.zip"), h);
    }
}

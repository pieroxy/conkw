package net.pieroxy.conkw.standalone;

import net.pieroxy.conkw.config.ConfigReader;
import net.pieroxy.conkw.utils.OsCheck;
import net.pieroxy.conkw.utils.StreamTools;
import net.pieroxy.conkw.utils.ZipUtil;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static net.pieroxy.conkw.utils.StreamTools.copyStreamAndClose;

public class Installer {

    public void run() throws Exception {
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));

        System.out.println("** INSTALLING CONKW **");
        System.out.println("Install conkw in " + ConfigReader.getHomeDir() + "? (Y/n)");
        String name = reader.readLine();
        if (name.equalsIgnoreCase("y") || name.equalsIgnoreCase("yes") || name.isEmpty()) {
            System.out.println("Installing conkw in " + ConfigReader.getHomeDir());
            doInstall();
            printInstructions();
        } else {
            System.out.println("Run this program again with the CONKW_HOME env variable set to the place you wish to install it. Remember to always set this env variable to the same path whenever you run conkw.");
        }
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
        Files.createDirectories(ConfigReader.getConfDir().toPath());
        StreamTools.copyTextFilePreserveOriginalAndWarnOnStdout(
                getClass().getClassLoader().getResourceAsStream("config.sample.jsonc"),
                ConfigReader.getConfigFile());
        StreamTools.copyTextFilePreserveOriginalAndWarnOnStdout(
                getClass().getClassLoader().getResourceAsStream("logging.properties"),
                ConfigReader.getLoggingConfigFile());
        Files.createDirectories(ConfigReader.getDataDir().toPath());
        Files.createDirectories(ConfigReader.getTmpDir().toPath());
        Files.createDirectories(ConfigReader.getBinDir().toPath());
        Files.createDirectories(ConfigReader.getLogDir().toPath());
        Files.createDirectories(ConfigReader.getUiDir().toPath());
        Files.createDirectories(ConfigReader.getWebappDir().toPath());
        initWebapp();
        Files.copy(
                new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).toPath(),
                new File(ConfigReader.getBinDir(), "conkw.jar").toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        FileOutputStream script = new FileOutputStream(new File(ConfigReader.getBinDir(), getFilename()));
        script.write(("\"" + java + "/bin/java\" -verbose:gc \"-Djava.util.logging.config.file="+ConfigReader.getLoggingConfigFile().getAbsolutePath()+"\" -Xms50m -jar \"" + ConfigReader.getBinDir() + File.separator + "conkw.jar\" --run-server >> \"" + ConfigReader.getLogDir() + "/system.log\" 2>&1\n").getBytes());
        script.close();
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

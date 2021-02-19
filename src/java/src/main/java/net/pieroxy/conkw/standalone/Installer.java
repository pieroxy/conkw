package net.pieroxy.conkw.standalone;

import net.pieroxy.conkw.config.ConfigReader;
import net.pieroxy.conkw.utils.ZipUtil;

import java.io.*;
import java.nio.file.Files;

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
        } else {
            System.out.println("Run this program again with the CONKW_HOME env variable set to the place you wish to install it. Remember to always set this env variable to the same path whenever you run conkw.");
            System.exit(1);
        }
    }

    private void doInstall() throws Exception {
        Files.createDirectories(ConfigReader.getConfDir().toPath());
        copyStreamAndClose(
                getClass().getClassLoader().getResourceAsStream("config.sample.jsonc"),
                new FileOutputStream(ConfigReader.getConfigFile()));
        Files.createDirectories(ConfigReader.getDataDir().toPath());
        Files.createDirectories(ConfigReader.getTmpDir().toPath());
        Files.createDirectories(ConfigReader.getUiDir().toPath());
        Files.createDirectories(ConfigReader.getWebappDir().toPath());
        initWebapp();
    }

    private void initWebapp() throws IOException {
        File h = ConfigReader.getWebappDir();
        File webinf = new File(h, "WEB-INF");
        Files.createDirectories(webinf.toPath());
        copyStreamAndClose(
                getClass().getClassLoader().getResourceAsStream("web.xml"),
                new FileOutputStream(new File(webinf, "web.xml")));
        ZipUtil.unzip(getClass().getClassLoader().getResourceAsStream("webapp-static.zip"), h);
    }

}

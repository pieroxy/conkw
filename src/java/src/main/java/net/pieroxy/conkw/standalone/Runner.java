package net.pieroxy.conkw.standalone;

import net.pieroxy.conkw.config.Config;
import net.pieroxy.conkw.config.ConfigReader;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class Runner {
    public static void main(String[] args) throws Exception {
        if (!has(args, "--run-server")) {
            if (!ConfigReader.exists() || has(args,"--force-reinstall")) {
                new Installer().run();
            } else {
                System.out.println("Conkw installation looks complete. To reinstall it use the --force-reinstall flag.");
            }
        } else {
            Config config = ConfigReader.getConfig();
            File webappDirLocation = ConfigReader.getWebappDir();
            Tomcat tomcat = new Tomcat();

            int webPort = config.getHttpPort();
            if (webPort <= 0) throw new Exception("httpPort configured to an invalid value of " + webPort);

            Connector ctr = new Connector();
            ctr.setPort(webPort);

            tomcat.setConnector(ctr);

            System.out.println("Configuring app with basedir: " + webappDirLocation.getAbsolutePath());
            StandardContext ctx = (StandardContext) tomcat.addWebapp("", webappDirLocation.getAbsolutePath());

            tomcat.start();
            tomcat.getServer().await();
        }
    }

    private static boolean has(String[] args, String lookFor) {
        for(String s : args) if (s.equals(lookFor)) return true;
        return false;
    }
}

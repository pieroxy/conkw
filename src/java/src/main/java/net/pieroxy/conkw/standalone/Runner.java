package net.pieroxy.conkw.standalone;

import net.pieroxy.conkw.config.Config;
import net.pieroxy.conkw.config.ConfigReader;
import net.pieroxy.conkw.webapp.Listener;
import net.pieroxy.conkw.webapp.servlets.Api;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.util.logging.Logger;

public class Runner {
    private final static Logger LOGGER = Logger.getLogger(Runner.class.getName());

    public static void main(String[] args) throws Exception {
        if (has(args, "--run-server")) {
            Config config = ConfigReader.getConfig();

            int webPort = config.getHttpPort();
            if (webPort <= 0) throw new Exception("httpPort configured to an invalid value of " + webPort);

            Tomcat tomcat = buildTomcat(ConfigReader.getWebappDir(), ConfigReader.getUiDir(), webPort);

            tomcat.start();
            tomcat.getServer().await();
        } else {
            if (!ConfigReader.exists() || has(args,"--force-install")) {
                new Installer(has(args,"--override-config-files")).run();
            } else {
                System.out.println("Conkw installation looks complete. Here are the flags you can use:");
                System.out.println("");
                System.out.println("  --force-install         : Forces installation to happen again.");
                System.out.println("  --override-config-files : Combined with the above flag, resets the");
                System.out.println("                            configuration.");
                System.out.println("");
                System.out.println("In both cases, if --force-install the current config file will be kept, either");
                System.out.println("as the actual config, or as the backup config if --override-config-files");
                System.out.println("is used.");
            }
        }
    }

    private static Tomcat buildTomcat(File webappDir, File uiDir, int webPort) {
        Tomcat tomcat = new Tomcat();

        Connector ctr = new Connector();
        ctr.setPort(webPort);
        tomcat.setConnector(ctr);

        LOGGER.info("Configuring app with basedir: " + webappDir.getAbsolutePath());

        addMainContext(webappDir, tomcat);
        addUiContext(uiDir, tomcat);
        return tomcat;
    }

    private static void addUiContext(File uiDir, Tomcat tomcat) {
        String contextPath = "/ui";
        StandardContext ctx = (StandardContext) tomcat.addContext(contextPath, uiDir.getAbsolutePath());
        ctx.addWelcomeFile("index.html");
        tomcat.addServlet(contextPath, "default", new DefaultServlet());
        ctx.addServletMappingDecoded("/", "default");
        ctx.addMimeMapping("svg", "image/svg+xml");
    }

    private static void addMainContext(File webappDirLocation, Tomcat tomcat) {
        String contextPath = "";
        StandardContext ctx = (StandardContext) tomcat.addContext(contextPath, webappDirLocation.getAbsolutePath());
        ctx.addWelcomeFile("index.html");

        tomcat.addServlet(contextPath, "api", new Api());
        ctx.addServletMappingDecoded("/api", "api");

        tomcat.addServlet(contextPath, "default", new DefaultServlet());
        ctx.addServletMappingDecoded("/", "default");

        ctx.addMimeMapping("svg", "image/svg+xml");

        ctx.addApplicationLifecycleListener(new Listener());
    }

    private static boolean has(String[] args, String lookFor) {
        for(String s : args) if (s.equals(lookFor)) return true;
        return false;
    }
}

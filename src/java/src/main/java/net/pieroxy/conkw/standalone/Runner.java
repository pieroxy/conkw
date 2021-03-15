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
            File webappDirLocation = ConfigReader.getWebappDir();
            Tomcat tomcat = new Tomcat();

            int webPort = config.getHttpPort();
            if (webPort <= 0) throw new Exception("httpPort configured to an invalid value of " + webPort);

            Connector ctr = new Connector();
            ctr.setPort(webPort);
            tomcat.setConnector(ctr);

            LOGGER.info("Configuring app with basedir: " + webappDirLocation.getAbsolutePath());

            String contextPath = "/";
            StandardContext ctx = (StandardContext) tomcat.addContext(contextPath, webappDirLocation.getAbsolutePath());
            ctx.addWelcomeFile("index.html");

            tomcat.addServlet(contextPath, "api", new Api());
            ctx.addServletMappingDecoded("/api", "api");

            tomcat.addServlet(contextPath, "default", new DefaultServlet());
            ctx.addServletMappingDecoded("/", "default");

            ctx.addApplicationLifecycleListener(new Listener());

            tomcat.start();
            tomcat.getServer().await();
        } else {
            if (!ConfigReader.exists() || has(args,"--force-install")) {
                new Installer(has(args,"--override-config-modifications")).run();
            } else {
                System.out.println("Conkw installation looks complete. To reinstall it use the --force-reinstall flag.");
            }
        }
    }

    private static boolean has(String[] args, String lookFor) {
        for(String s : args) if (s.equals(lookFor)) return true;
        return false;
    }
}

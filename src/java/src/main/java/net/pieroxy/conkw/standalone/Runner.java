package net.pieroxy.conkw.standalone;

import net.pieroxy.conkw.config.Config;
import net.pieroxy.conkw.config.ConfigReader;
import net.pieroxy.conkw.utils.HashTools;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.webapp.Listener;
import net.pieroxy.conkw.webapp.servlets.Api;
import net.pieroxy.conkw.webapp.servlets.Emi;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Runner {
    private final static Logger LOGGER = Logger.getLogger(Runner.class.getName());

    private static Tomcat tomcat;

    public static void main(String[] args) throws Exception {
        CmdLineOptions options = new CmdLineOptions((args));
        if (options.getHome()!=null) ConfigReader.defineHomeDirectory(options.getHome());

        switch (options.getAction()) {
            case CmdLineOptions.ACTION_START:
                run(options);
                break;
            case CmdLineOptions.ACTION_STOP:
                stop(options);
                break;
            case CmdLineOptions.ACTION_INSTALL:
                install(options);
                break;
            default:
                System.out.println("Usage:");
                System.out.println("java -jar conkw.jar ACTION [OPTIONS...]");
                System.out.println();
                System.out.println("ACTION start");
                System.out.println("    Starts conkw service.");
                System.out.println("    Options:");
                System.out.println("      --stopCurrentInstance");
                System.out.println("          Conkw will stop the current instance if any is running and start a new");
                System.out.println("          one. SOON.");
                System.out.println("");
                System.out.println("ACTION stop");
                System.out.println("    Stops the currently running conkw instance if any. SOON.");
                System.out.println("");
                System.out.println("ACTION install");
                System.out.println("    Installs conkw");
                System.out.println("    Options:");
                System.out.println("      --upgrade");
                System.out.println("        Upgrades an existing installation if any.");
                System.out.println("      --override-default-ui");
                System.out.println("        Resets the default UI.");
                System.out.println("      --override-config-files");
                System.out.println("        Resets the configuration.");
                System.out.println("");
                System.out.println("Global options :");
                System.out.println("      --home location/of/conkw/home/folder");
                System.out.println("        Defines the installation folder. Overrides the CONKW_HOME env var.");
        }
    }

    private static void install(CmdLineOptions options) throws Exception {
        if (ConfigReader.exists() && !options.isUpgrade()) {
            System.out.println("There is already a conkw install in " + ConfigReader.getHomeDir());
            System.out.println("Please run with --upgrade to install anyway.");
            return;
        }
        new Installer(options.isOverrideConfig(), options.isOverrideUi()).run();
    }

    private static void stop(CmdLineOptions options) throws IOException {
        System.out.println("stop command is not implemented yet.");
    }

    private static void run(CmdLineOptions options) throws Exception {
        saveInstanceId();
        Config config = ConfigReader.getConfig();

        if (config.getHttpPort() <= 0) throw new Exception("httpPort configured to an invalid value of " + config.getHttpPort());

        if (config.disableTomcat()) {
            Listener listener = new Listener();
            listener.contextInitialized(null);
            synchronized (Runner.class) {
                Runner.class.wait();
            }
        } else {
            tomcat = buildTomcat(ConfigReader.getWebappDir(), ConfigReader.getUiDir(), config);
            tomcat.start();
            tomcat.getServer().await();
        }
    }

    private static void saveInstanceId() {
        String key = HashTools.getRandomSequence(10);
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        InstanceId iid = new InstanceId(pid, key);
        Api.configureShutdownHook(iid, () -> shutdown());
        File out = getInstanceIdFile();
        try {
            out.setWritable(true, true);
            JsonHelper.writeToFile(iid, out);
            out.setExecutable(false, false);
            out.setWritable(false, false);
            out.setReadable(false, false);
            out.setReadable(true, true);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not serialize the instance id", e);
        }
    }

    private static File getInstanceIdFile() {
        return new File(ConfigReader.getConfDir(), "instanceid.json");
    }

    private static void shutdown() {
        try {
            LOGGER.log(Level.INFO, "Shutting Down.");
            tomcat.stop();
            tomcat.destroy();
        } catch (LifecycleException e) {
            LOGGER.log(Level.SEVERE, "Stopping Tomcat", e);
        }
    }

    private static Tomcat buildTomcat(File webappDir, File uiDir, Config config) {
        Tomcat tomcat = new Tomcat();

        String tempDir = System.getProperty("java.io.tmpdir");
        if (tempDir!=null) {
            tomcat.setBaseDir(tempDir + File.separator + "conkwTomcat");
        }

        Connector ctr = new Connector();
        ctr.setPort(config.getHttpPort());
        tomcat.setConnector(ctr);

        LOGGER.info("Configuring app with basedir: " + webappDir.getAbsolutePath());

        addMainContext(webappDir, tomcat, config);
        addUiContext(uiDir, tomcat, config);
        return tomcat;
    }

    private static void addUiContext(File uiDir, Tomcat tomcat, Config config) {
        if (!config.isDisableCustomUI()) {
            LOGGER.info("Registering custom UI");
            String contextPath = "/ui";
            StandardContext ctx = (StandardContext) tomcat.addContext(contextPath, uiDir.getAbsolutePath());
            ctx.addWelcomeFile("index.html");
            tomcat.addServlet(contextPath, "default", new DefaultServlet());
            ctx.addServletMappingDecoded("/", "default");
            ctx.addMimeMapping("svg", "image/svg+xml");
        }
    }

    private static void addMainContext(File webappDirLocation, Tomcat tomcat, Config config) {
        String contextPath = "";
        StandardContext ctx = (StandardContext) tomcat.addContext(contextPath, webappDirLocation.getAbsolutePath());

        if (!config.isDisableApi()) {
            tomcat.addServlet(contextPath, "api", new Api());
            ctx.addServletMappingDecoded("/api", "api");
        }

        if (!config.isDisableEmi()) {
            tomcat.addServlet(contextPath, "emi", new Emi());
            ctx.addServletMappingDecoded("/emi", "emi");
        }

        if (!config.isDisableDefaultUI()) {
            LOGGER.info("Registering default UI");
            ctx.addWelcomeFile("index.html");
            tomcat.addServlet(contextPath, "default", new DefaultServlet());
            ctx.addServletMappingDecoded("/", "default");

            ctx.addMimeMapping("svg", "image/svg+xml");
        }

        ctx.addApplicationLifecycleListener(new Listener());
    }

    private static boolean has(String[] args, String lookFor) {
        for(String s : args) if (s.equals(lookFor)) return true;
        return false;
    }
}

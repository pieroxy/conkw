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
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Runner {
    private final static Logger LOGGER = Logger.getLogger(Runner.class.getName());
    private final static int STOP_COMPLETE  = 1;
    private final static int STOP_NOOP  = 2;
    private final static int STOP_FAILED  = 3;

    private static Tomcat tomcat;

    public static void main(String[] args) throws Exception {
        CmdLineOptions options = new CmdLineOptions((args));
        if (options.getHome()!=null) ConfigReader.defineHomeDirectory(options.getHome());

        switch (options.getAction()) {
            case CmdLineOptions.ACTION_START:
                run(options);
                break;
            case CmdLineOptions.ACTION_STOP:
                stop();
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
                System.out.println("      --stop-current-instance");
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
        if (!stop()) {
            return; // Cannot install if the process is actually running - files are locked.
        }
        new Installer(options.isOverrideConfig(), options.isOverrideUi()).run();
    }

    /**
     *
     * @return true if the existing process is stopped, false if it failed.
     * @throws IOException
     */
    private static boolean stop() throws IOException {
        InstanceId iid = JsonHelper.readFromFile(InstanceId.class, getInstanceIdFile());
        Config conf = ConfigReader.getConfig();
        switch (stopLoop(conf, iid)) {
            case STOP_COMPLETE:
                LOGGER.info("Current instance was successfully stopped.");
                return true;
            case STOP_NOOP:
                LOGGER.info("No current instance could be detected.");
                return true;
            case STOP_FAILED:
                LOGGER.severe("Current instance could not be stopped.");
                return false;
            default:
                LOGGER.severe("Something went bonkers.");
                return false;
        }
    }

    private static int stopLoop(Config conf, InstanceId iid) throws IOException {
        int loopNumber = 0;

        while (loopNumber < 20) {
            if (portAlreadyTaken(conf)) {
            } else {
                return loopNumber == 0 ? STOP_NOOP : STOP_COMPLETE;
            }
            if (loopNumber == 0) {
                URL u = new URL("http://localhost:" + conf.getHttpPort() + "/api?shutdown=" + iid.key);
                HttpURLConnection c = (HttpURLConnection) u.openConnection();
                try {
                    c.connect();
                    int rc = c.getResponseCode();
                } catch (Exception e) {
                }
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }

            loopNumber++;
        }
        return STOP_FAILED;
    }

    private static boolean portAlreadyTaken(Config conf) throws IOException {
        URL u = new URL("http://localhost:" + conf.getHttpPort() + "/");
        HttpURLConnection c = (HttpURLConnection)u.openConnection();
        try {
            c.connect();
        } catch (ConnectException e) {
            return false;
        } catch (Exception e) {
        }
        return true;
    }

    private static void run(CmdLineOptions options) throws Exception {
        if (options.isStopCurrentInstance()) {
            if (!stop()) {
                return;
            }
        }
        if (portAlreadyTaken(ConfigReader.getConfig())) {
            System.out.println("The port " + ConfigReader.getConfig().getHttpPort() + " is already taken. Run with --stop-current-instance to stop the current instance first.");
            return;
        }
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

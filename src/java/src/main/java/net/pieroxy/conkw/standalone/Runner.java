package net.pieroxy.conkw.standalone;

import net.pieroxy.conkw.config.Config;
import net.pieroxy.conkw.config.ConfigReader;
import net.pieroxy.conkw.utils.FileTools;
import net.pieroxy.conkw.utils.HashTools;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.logging.GcLogging;
import net.pieroxy.conkw.utils.logging.LoggingPrintStream;
import net.pieroxy.conkw.webapp.Filter;
import net.pieroxy.conkw.webapp.Listener;
import net.pieroxy.conkw.webapp.servlets.Api;
import net.pieroxy.conkw.webapp.servlets.Emi;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import java.io.*;
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
    private final static String GIT_REV;
    private final static String MVN_VER;

    static {
        GIT_REV = readResourceFileAsString("GIT_REV");
        MVN_VER = readResourceFileAsString("MVN_VER");
    }

    private static String readResourceFileAsString(String filename) {
        try {
            InputStream is = Runner.class.getClassLoader().getResourceAsStream(filename);
            return new BufferedReader(new InputStreamReader(is)).lines().findFirst().get();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Could not read " + filename, e);
            return filename + "_" + Math.random();
        }
    }

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
            System.out.println("Running instance takes longer than expected to stop. Press Ctrl-C to abort this.");
            if (!stop() && !stop()) {
                return; // Cannot install if the process is actually running - files are locked.
            }
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

        if (conf.getHttpPort() == -1 || iid == null) {
            return STOP_NOOP;
        }

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
        System.setOut(new LoggingPrintStream("stdout", Level.INFO));
        System.setErr(new LoggingPrintStream("stderr", Level.SEVERE));
        GcLogging.installGCMonitoring();
        LOGGER.log(Level.INFO, String.format("Starting version %s (build %s)", MVN_VER, GIT_REV));
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
            FileTools.makeFileWritableForUser(out);
            JsonHelper.writeToFile(iid, out);
            FileTools.makeFileReadonlyForUser(out);
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
        ctr.setProperty("compression", "on");
        ctr.setProperty("compressionMinSize", "512");
        ctr.setProperty("compressibleMimeType", "text/html, text/css, application/javascript, image/svg+xml" + (config.isEnableApiCompression() ? ", application/json" : ""));

        LOGGER.info("Configuring app with basedir: " + webappDir.getAbsolutePath());

        FilterDef fd = new FilterDef();
        fd.setFilter(new Filter());
        fd.setFilterName("http_log_filter");

        FilterMap fm = new FilterMap();
        fm.setFilterName("http_log_filter");
        fm.addURLPattern("/*");

        addMainContext(webappDir, tomcat, config, fd, fm);
        addUiContext(uiDir, tomcat, config, fd, fm);
        return tomcat;
    }

    private static void addUiContext(File uiDir, Tomcat tomcat, Config config, FilterDef fd, FilterMap fm) {
        if (!config.isDisableCustomUI()) {
            LOGGER.info("Registering custom UI");
            String contextPath = "/ui";
            StandardContext ctx = (StandardContext) tomcat.addContext(contextPath, uiDir.getAbsolutePath());
            ctx.addWelcomeFile("index.html");
            tomcat.addServlet(contextPath, "default", new DefaultServlet());
            ctx.addServletMappingDecoded("/", "default");
            addMimeTypes(ctx);
            ctx.addFilterDef(fd);
            ctx.addFilterMap(fm);
        }
    }

    private static void addMimeTypes(StandardContext ctx) {
        ctx.addMimeMapping("js", "application/javascript;charset=utf-8");
        ctx.addMimeMapping("html", "text/html;charset=utf-8");
        ctx.addMimeMapping("css", "text/css;charset=utf-8");

        ctx.addMimeMapping("svg", "image/svg+xml");
        ctx.addMimeMapping("png", "image/png");
        ctx.addMimeMapping("jpg", "image/jpeg");
        ctx.addMimeMapping("jpeg", "image/jpeg");
        ctx.addMimeMapping("gif", "image/gif");
        ctx.addMimeMapping("ico", "image/x-icon");
        ctx.addMimeMapping("woff", "font/woff");
        ctx.addMimeMapping("webmanifest", "application/manifest+json");
    }

    private static void addMainContext(File webappDirLocation, Tomcat tomcat, Config config, FilterDef fd, FilterMap fm) {
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

            addMimeTypes(ctx);
        }

        ctx.addApplicationLifecycleListener(new Listener());
        ctx.addFilterDef(fd);
        ctx.addFilterMap(fm);
    }

    private static boolean has(String[] args, String lookFor) {
        for(String s : args) if (s.equals(lookFor)) return true;
        return false;
    }
}

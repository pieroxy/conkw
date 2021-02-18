package net.pieroxy.conkw.standalone;

import net.pieroxy.conkw.config.Config;
import net.pieroxy.conkw.config.ConfigReader;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

public class Runner {
    public static void main(String[] args) throws Exception {
        if (!ConfigReader.exists()) {
            new Installer().run();
        }

        Config config = ConfigReader.getConfig();
        File webappDirLocation = ConfigReader.getWebappDir();
        Tomcat tomcat = new Tomcat();

        int webPort = config.getHttpPort();
        if (webPort <= 0) throw new Exception("httpPort configured to an invalid value of " + webPort);

        //tomcat.setPort(Integer.valueOf(webPort));

        Connector ctr = new Connector();
        ctr.setPort(webPort);

        tomcat.setConnector(ctr);

        System.out.println("Configuring app with basedir: " + webappDirLocation.getAbsolutePath());
        StandardContext ctx = (StandardContext) tomcat.addWebapp("", webappDirLocation.getAbsolutePath());

        // Declare an alternative location for your "WEB-INF/classes" dir
        // Servlet 3.0 annotation will work
        /*File additionWebInfClasses = new File("target/classes");
        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                additionWebInfClasses.getAbsolutePath(), "/"));
        ctx.setResources(resources);*/

        tomcat.start();
        tomcat.getServer().await();
    }
}

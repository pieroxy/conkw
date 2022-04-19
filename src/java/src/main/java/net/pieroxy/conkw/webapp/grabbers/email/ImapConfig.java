package net.pieroxy.conkw.webapp.grabbers.email;

import net.pieroxy.conkw.config.Credentials;
import net.pieroxy.conkw.config.CredentialsProvider;
import net.pieroxy.conkw.utils.hashing.Hashable;
import net.pieroxy.conkw.utils.hashing.Md5Sum;

public class ImapConfig implements Hashable, CredentialsProvider {
    private String server,name;
    private Credentials credentials;
    private String credentialsRef;
    private Double port;

    public ImapConfig() {
    }

    @Override
    public String toString() {
        return "ImapConfig{" +
            "server='" + server + '\'' +
            ", name='" + name + '\'' +
            ", credentials=" + credentials +
            ", credentialsRef='" + credentialsRef + '\'' +
            ", port=" + port +
            '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Double getPort() {
        return port;
    }

    public void setPort(Double port) {
        this.port = port;
    }

    @Override
    public void addToHash(Md5Sum sum) {
        sum.add(getCredentialsRef());
        if (credentials!=null) credentials.addToHash(sum);
        sum.add(getName());
        sum.add(getServer());
        sum.add(String.valueOf(getPort()));
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public String getCredentialsRef() {
        return credentialsRef;
    }

    public void setCredentialsRef(String credentialsRef) {
        this.credentialsRef = credentialsRef;
    }
}

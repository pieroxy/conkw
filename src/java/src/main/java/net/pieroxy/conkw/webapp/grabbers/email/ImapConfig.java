package net.pieroxy.conkw.webapp.grabbers.email;

public class ImapConfig {
    private String name;
    private String server;
    private int port;
    private String login;
    private String password;

    public ImapConfig(String conf) {
        int p1 = conf.indexOf(':');
        int p2 = conf.indexOf(':', p1+1);
        int p3 = conf.indexOf(':', p2+1);
        int p4 = conf.indexOf(':', p3+1);
        setName(conf.substring(0, p1));
        setServer(conf.substring(p1 + 1, p2));
        setPort(Integer.parseInt(conf.substring(p2 + 1, p3)));
        setLogin(conf.substring(p3 + 1, p4));
        setPassword(conf.substring(p4 + 1));
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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

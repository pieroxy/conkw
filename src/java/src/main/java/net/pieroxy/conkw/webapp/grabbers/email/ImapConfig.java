package net.pieroxy.conkw.webapp.grabbers.email;

import java.util.Map;

public class ImapConfig {
    private String server,login,password,name;
    private int port;

    public ImapConfig(String name, Map<String, String> conf) {
        setName(name);
        setServer(conf.get("server"));
        setPort(Integer.parseInt(conf.get("port")));
        setLogin(conf.get("login"));
        setPassword(conf.get("password"));
    }

    public String getUniqueContentString() {
        return name + server + port + login + password;
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

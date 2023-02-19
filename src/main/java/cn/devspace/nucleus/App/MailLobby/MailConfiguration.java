package cn.devspace.nucleus.App.MailLobby;

import cn.devspace.nucleus.Manager.SettingManager;

import java.util.Map;

public class MailConfiguration {
    private String hostName;
    private int port;
    private String username;
    private String password;
    private String sender;
    private String auth;
    private String method;

    public MailConfiguration() {
        SettingManager settingManager = new SettingManager();
        Map<String,String> map = settingManager.getMapSetting("mail.lobby");
        this.username = map.get("user");
        this.password = map.get("password");
        this.hostName = map.get("host");
        this.port = Integer.parseInt(map.get("port"));
        this.method = map.get("method");
        this.sender = map.get("sender");
        this.auth = map.get("auth");
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}

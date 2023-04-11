package cn.devspace.nucleus.App.MailLobby.unit;

import cn.devspace.nucleus.App.MailLobby.Pool.mailPool;
import cn.devspace.nucleus.App.MailLobby.Threads.EmailQueue;
import cn.devspace.nucleus.Manager.SettingManager;
import cn.devspace.nucleus.Message.Log;
import org.apache.commons.mail.*;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

public class sendMail {

    private String user;
    private String password;
    private String host;
    private String port;
    private String protocol;
    private String auth;
    private int timeout = 10000;

    private String sender;


    public sendMail() {
        SettingManager settingManager = new SettingManager();
        Map<String,String> map = settingManager.getMapSetting("mail.lobby");
        this.user = map.get("user");
        this.password = map.get("password");
        this.host = map.get("host");
        this.port = map.get("port");
        this.protocol = map.get("method");
        this.sender = map.get("sender");
        this.auth = map.get("auth");
    }

    public boolean sendSimpleEmail(String to, String subject, String content)  {
        try {
            // 支持html
            Email email = new HtmlEmail();
            //配置文件
            email.setHostName(host);
            email.setSmtpPort(Integer.parseInt(port));
            email.setAuthenticator(new DefaultAuthenticator(user, password));
            email.setSSLOnConnect(true);
            email.setSslSmtpPort(port);
            email.setCharset("UTF-8");
            email.setFrom(user,sender);
            email.setSubject(subject);
            email.setMsg(content);
            email.addTo(to);
            EmailQueue.getInstance().addEmail(email);
        } catch (EmailException e) {
           Log.sendWarn(e.toString());
           return false;
        }
        return true;
        }

    }


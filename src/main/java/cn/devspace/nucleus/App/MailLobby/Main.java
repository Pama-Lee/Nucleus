package cn.devspace.nucleus.App.MailLobby;

import cn.devspace.nucleus.App.MailLobby.unit.sendMail;
import cn.devspace.nucleus.Lang.LangBase;
import cn.devspace.nucleus.Plugin.AppBase;

import java.util.Map;

public class Main extends AppBase {

    public LangBase langBase;
    private Map<String, Object> config;
    @Override
    public void onLoad() {
        this.langBase = loadLanguage();
        this.config = getConfig();
        sendLog(langBase.TranslateOne("Loading"));
        sendMail sendMail = new sendMail();
       // sendMail.sendSimpleEmail("eee2209292@xmu.edu.my","test","test");

    }
}

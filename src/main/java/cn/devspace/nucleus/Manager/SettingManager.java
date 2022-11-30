package cn.devspace.nucleus.Manager;

import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Server.Server;

import java.util.Map;

public class SettingManager extends ManagerBase {


    public String getSetting(String key) {
        Map<String, String> Map = getSingeYaml(Server.RunPath + "resources/nucleus.yml");
        Log.sendLog(Map.get(key));
        return Map.get(key);
    }

}

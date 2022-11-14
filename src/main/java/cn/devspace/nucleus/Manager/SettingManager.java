package cn.devspace.nucleus.Manager;

import java.util.Map;

public class SettingManager extends ManagerBase {


    public String getSetting(String key) {
        Map<String, String> Map = super.getSingeYaml(System.getProperty("user.dir") + "/resources/nucleus.yml");
        return Map.get(key);
    }

}

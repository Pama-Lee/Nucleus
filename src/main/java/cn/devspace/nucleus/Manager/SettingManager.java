package cn.devspace.nucleus.Manager;

import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Server.Server;

import java.util.ArrayList;
import java.util.Map;

public class SettingManager extends ManagerBase {

    public String getSetting(String key) {
        Map<String, String> Map = getSingeYaml(Server.RunPath + "resources/nucleus.yml");
        return Map.get(key);
    }

    public ArrayList<String> getSetting(String key, boolean array){
        Map<String,ArrayList<String>> map = getSingeYaml(Server.RunPath + "resources/nucleus.yml",true);
        return map.get(key);
    }

    public Map<String,String> getMapSetting(String key){
        Map<String,Map<String,String>> map = getSingeYamlMap(Server.RunPath + "resources/nucleus.yml");
        return map.get(key);
    }

}

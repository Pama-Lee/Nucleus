package cn.devspace.nucleus.Manager;

import cn.devspace.nucleus.Lang.LangBase;
import cn.devspace.nucleus.Message.Log;
import com.google.gson.Gson;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManagerBase extends LangBase {

    public String Map2Json(Map<String, String> Map) {
        Gson gson = new Gson();
        return gson.toJson(Map);
    }

    public Map<String, String> makeResponse(int code, int status, String Message) {
        Map<String, String> Response = new HashMap<>();
        Response.put("code", String.valueOf(code));
        Response.put("status", String.valueOf(status));
        Response.put("message", Message);
        return Response;
    }

    public String ResponseString(int code, int status, String Message) {
        return this.Map2Json(this.makeResponse(code, status, Message));
    }

    public static Map<String, String> getSingeYaml(String Path) {
        Yaml yaml = new Yaml();
        BufferedReader bf = null;
        try {
            bf = new BufferedReader(new FileReader(Path));
        } catch (FileNotFoundException e) {
            Log.sendWarn("配置文件找不到");
        }
        return yaml.loadAs(bf, Map.class);
    }

    public static Map<String, String> getSingeYaml(InputStream inputStream) {
        Yaml yaml = new Yaml();
        BufferedReader bf = null;
        bf = new BufferedReader(new InputStreamReader(inputStream));
        Map<String, String> Map = yaml.loadAs(bf, Map.class);
        return Map;
    }

    public static Map<String, ArrayList<String>> getSingeYaml(String Path, boolean ArrayList) {
        Yaml yaml = new Yaml();
        BufferedReader bf = null;
        try {
            bf = new BufferedReader(new FileReader(Path));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Map<String, ArrayList<String>> Map = yaml.loadAs(bf, Map.class);
        return Map;
    }

    public String Translators(String key) {
        return TranslateOne(key);
    }

    public String getJarPath() {
        String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        if (System.getProperty("os.name").contains("dows")) {
            path = path.substring(1, path.length());
        }
        if (path.contains("jar")) {
            path = path.substring(0, path.lastIndexOf("."));
            return path.substring(0, path.lastIndexOf("/"));
        }
        return path.replace("target/classes/", "");
    }

    public String Translators(String key, Object... params) {
        return TranslateOne(key, params);
    }

    public String getLangSet() {
        return getSingeYaml(this.getClass().getResource("/nucleus.yml").getPath().substring(1)).get("app.language");
    }
}

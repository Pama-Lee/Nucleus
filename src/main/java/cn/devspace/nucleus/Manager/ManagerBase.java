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

    /**
     * 将Map数据转为json
     * @param Map 传入Map数据
     * @return 返回String类型json
     */
    public static String Map2Json(Map<String, String> Map) {
        Gson gson = new Gson();
        return gson.toJson(Map);
    }

    public static String Map2Json(Map<String, Object> Map, boolean isObject) {
        Gson gson = new Gson();
        return gson.toJson(Map);
    }

    /**
     * 构造简单的返回数据
     * @param code 返回的代码
     * @param status 返回的状态
     * @param Message 返回的信息
     * @return 返回构造完毕的Map数据
     */
    public Map<String, String> makeResponse(int code, int status, String Message) {
        Map<String, String> Response = new HashMap<>();
        Response.put("code", String.valueOf(code));
        Response.put("status", String.valueOf(status));
        Response.put("message", Message);
        return Response;
    }

    public Map<String, Object> makeResponse(int code, int status, String Message, Object data) {
        Map<String, Object> Response = new HashMap<>();
        Response.put("code", code);
        Response.put("status", status);
        Response.put("message", Message);
        Response.put("data", data);
        return Response;
    }

    /**
     * 狗仔简单的返回数据Json字符串
     * @param code 返回的代码
     * @param status 返回的状态
     * @param Message 返回的信息
     * @return 返回构造完毕的json字符串
     */
    public String ResponseString(int code, int status, String Message) {
        return Map2Json(this.makeResponse(code, status, Message));
    }

    /**
     * 构造简单的返回数据Json字符串
     * @param code 返回的代码
     * @param status 返回的状态
     * @param Message 返回的信息
     * @param data 返回的数据
     * @return 返回构造完毕的json字符串
     */
    public Object ResponseObject(int code, int status, String Message, Object data) {
        return Map2Json(makeResponse(code, status, Message, data), true);
    }

    /**
     * 获取简单的Yaml配置文件
     * @param Path 配置文件地址
     * @return 返回key和value均为string的数据
     */
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

    public static Map<String, Map<String,String>> getSingeYamlMap(String Path) {
        Yaml yaml = new Yaml();
        BufferedReader bf = null;
        try {
            bf = new BufferedReader(new FileReader(Path));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Map<String, Map<String,String>> map = yaml.loadAs(bf, Map.class);
        return map;
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

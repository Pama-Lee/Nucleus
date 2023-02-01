package cn.devspace.nucleus.Units;

import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Server.Server;
import com.google.gson.Gson;
import org.apache.commons.lang.ClassUtils;
import org.apache.tomcat.Jar;

import java.io.File;
import java.io.Serial;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Unit {
    public static Map<String ,Object> json2Map(String json){
        Map<String, Object> map = new HashMap<String, Object>();
        map = (Map<String, Object>) new Gson().fromJson(json,Map.class);
        return map;
    }

    public static <T> boolean isStartupFromJar(Class<T> clazz) {
        File file = new File(clazz.getProtectionDomain().getCodeSource().getLocation().getPath());
        return file.isFile();
    }

    public static boolean haveApp(String AppName){
        return Server.PluginList.containsKey(AppName) || Server.AppList.containsKey(AppName);
    }

    public static boolean checkParams(Map<String,String> maps, String[] params){
        for (String param : params) {
            if(!maps.containsKey(param)){
                return false;
            }
        }
        return true;
    }

    public static Set<String> getClassesFromJar(JarFile jar){
        Set<String> result = new HashSet<>();
        Enumeration<JarEntry> entrys = jar.entries();
        while (entrys.hasMoreElements()){
            JarEntry jarEntry = entrys.nextElement();
            if (jarEntry.getName().contains(".class")){
                String name = jarEntry.getName();
                name =  name.replace(".class","");
                name = name.replace("/",".");
                result.add(name);
            }
        }
        return result;
    }

}

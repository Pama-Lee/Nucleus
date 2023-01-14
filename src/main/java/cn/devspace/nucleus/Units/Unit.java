package cn.devspace.nucleus.Units;

import cn.devspace.nucleus.Server.Server;

import java.io.File;
import java.io.Serial;

public class Unit {

    public static <T> boolean isStartupFromJar(Class<T> clazz) {
        File file = new File(clazz.getProtectionDomain().getCodeSource().getLocation().getPath());
        return file.isFile();
    }

    public static boolean haveApp(String AppName){
        return Server.PluginList.containsKey(AppName) || Server.AppList.containsKey(AppName);
    }
}

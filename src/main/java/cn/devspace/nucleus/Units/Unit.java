package cn.devspace.nucleus.Units;

import java.io.File;

public class Unit {

    public static <T> boolean isStartupFromJar(Class<T> clazz) {
        File file = new File(clazz.getProtectionDomain().getCodeSource().getLocation().getPath());
        return file.isFile();
    }
}

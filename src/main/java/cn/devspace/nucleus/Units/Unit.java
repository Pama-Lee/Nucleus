package cn.devspace.nucleus.Units;

import cn.devspace.nucleus.Manager.ClassLoader.PluginClassLoader;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Server.Server;
import com.google.gson.Gson;
import org.apache.commons.lang.ClassUtils;
import org.apache.tomcat.Jar;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Serial;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static java.lang.annotation.RetentionPolicy.CLASS;

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

    /**
     * 加载指定路径下所有Class文件
     * @param classPath
     */
    public static void findFullClassName(String classPath, boolean recursive) throws NoSuchMethodException, MalformedURLException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        //遍历过滤好的文件
        for (File file : findFiles(classPath,recursive)) {
            //如果文件是目录
            if (file.isDirectory()) {
                //调用当前方法
                findFullClassName(file.getPath(), true);  //路径为文件路径
            }else{
                Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                //如果是class文件 则去除 后缀 .class
                Server.devPluginClass.add(file.toURI().toURL());
                // 将当前类路径加入到类加载器中
                String className = file.getName().substring(0, file.getName().lastIndexOf(".class"));
                //添加包名到list
            }
        }
    }
    /**
     * 获取路径下所有的 .class 文件 和  文件夹
     *
     * @param filePath  文件目录
     * @param recursive 是否查询子文件夹
     * @return 文件
     */
    public static File[] findFiles(String filePath, boolean recursive) {
        //过滤出 class文件 和 目录
        return new File(filePath).listFiles(file ->
                (file.isFile() && file.getName().endsWith(".class")) ||
                        (file.isDirectory() && recursive));
    }


    public static ClassLoader createDirectoryLoader(String directory) throws URISyntaxException, IOException {
        Collection<URL> urls = new ArrayList<URL>();
        File dir = new File(directory);
        File[] files = dir.listFiles();
        for (File f : files) {
            System.out.println(f.getCanonicalPath());
            urls.add(f.toURI().toURL());
        }

        return URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]));
    }



}

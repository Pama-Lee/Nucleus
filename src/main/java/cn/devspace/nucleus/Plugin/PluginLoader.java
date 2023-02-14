package cn.devspace.nucleus.Plugin;

import cn.devspace.nucleus.Lang.LangBase;
import cn.devspace.nucleus.Manager.BeanManager;
import cn.devspace.nucleus.Manager.ClassLoader.DevClassLoader;
import cn.devspace.nucleus.Manager.ClassLoaderManager;
import cn.devspace.nucleus.Manager.ClassManager;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Server.Server;
import cn.devspace.nucleus.Units.Unit;
import org.apache.tomcat.Jar;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static cn.devspace.nucleus.Server.Server.RunPath;


public class PluginLoader implements Loader {


    protected Description description;

    private ClassLoaderManager classLoaderManager;

    protected static String LoadingApp;

    protected static String getPlugin;

    protected String PluginName;

    private Server server;
    public PluginLoader(Server server, String AppName, ClassLoaderManager classLoaderManager) {
        this.server = server;
        this.classLoaderManager = classLoaderManager;
    }

    public static Map<String, JarFile> getPluginJars() {
        Map<String, JarFile> res = new HashMap<>();
        String[] list = new File(RunPath + "plugins/").list();
        if (list != null) {
            for (String s : list) {
                try {
                    JarFile jarFile = new JarFile(new File(RunPath + "plugins/" + s));
                    res.put(s,jarFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
        return res;
    }

    public static JarFile getJar(String plugin){
        return getPluginJars().get(plugin);
    }


    public Description getDescription(JarFile pluginJar) {
        try {
            JarEntry entry = pluginJar.getJarEntry("nucleus.yml");
            InputStream stream = null;
            stream = pluginJar.getInputStream(entry);
            return new Description(stream);
        } catch (Exception ie) {
            Log.sendWarn("无法载入插件描述文件");
        }
        return null;
    }

    public Description getDescription(File nucleusYaml) {
        try {
            // 读取nucleus.yml文件
            InputStream stream = nucleusYaml.toURI().toURL().openStream();
            return new Description(stream);
        } catch (Exception ie) {
            Log.sendWarn("无法载入插件描述文件");
        }
        return null;
    }


    public Map<String, PluginBase> getDevPlugin() {
        // 获取plugins文件夹下的所有的文件夹
        if (!new File(RunPath + "plugins/").exists()) {
            return null;
        } else {
            Map<String, PluginBase> res = new HashMap<>();
            File[] list = new File(RunPath + "plugins/").listFiles();
            if (list != null) {
                for (File s : list) {
                    // 如果为文件夹, 则进入加载了流程
                    if (s.isDirectory()) {
                        // 寻找文件夹下的nucleus.yml文件
                        File nucleus = new File(s.getPath() + "/nucleus.yml");
                        // 如果存在这个, 则判定为插件
                        if (nucleus.exists()) {
                            Description description = getDescription(nucleus);
                            // 如果配置文件有效
                            if (description.isEnabled()) {

                                String main = description.getMain();
                                String pluginName = description.getName();
                                Log.sendLog(server.TranslateOne("Plugin.LoadDev", pluginName));
                                try {
                                    String classLoaderHashCode = classLoaderManager.createDevClassLoader(RunPath + "plugins/" + pluginName + "/");
                                    DevClassLoader urlClassLoader = (DevClassLoader) classLoaderManager.getClassLoader(classLoaderHashCode);
                                    Class<?> pluginClass = urlClassLoader.loadClass(main);
                                    // 如果插件类继承了PluginBase类
                                    if (PluginBase.class.isAssignableFrom(pluginClass)) {
                                        PluginBase pluginBase = (PluginBase) pluginClass.getConstructor().newInstance();

                                        Set<String> clazz = Unit.getClassesFromDir(new File(RunPath + "plugins/" + pluginName));
                                        pluginBase.allClazz = clazz;
                                        pluginBase.setDescription(description);
                                        pluginBase.classLoaderHashCode = classLoaderHashCode;
                                        pluginBase.setPluginName(description.getName());
                                        // 首选加载框架指定的语言文件
                                        String lang = server.getLanguage();
                                        File langFile = new File(s.getPath() + "/resources/language/" + lang + ".ini");
                                        if (langFile.exists()) {
                                            LangBase langBase = new LangBase(langFile.toURI().toURL().openStream());
                                            pluginBase.setPluginLang(langBase);
                                        } else {
                                            // 寻找插件的语言文件
                                            File PluginLang = new File(s.getPath() + "/resources/language/" + description.getLanguage() + ".ini");
                                            if (PluginLang.exists()) {
                                                LangBase langBase = new LangBase(PluginLang.toURI().toURL().openStream());
                                                pluginBase.setPluginLang(langBase);
                                            }
                                        }

                                        res.put(pluginName, pluginBase);

                                    } else {
                                        Log.sendWarn(Server.getInstance().TranslateOne("Plugin.Not.PluginBase", pluginName));
                                        continue;
                                    }
                                } catch (IOException | ClassNotFoundException | InvocationTargetException |
                                         InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                        }
                    }
                }
                return res;
            }
            return null;
        }
    }


    public Map<String, PluginBase> getPlugins() {
        if (!new File(RunPath + "plugins/").exists()) {
            return null;
        } else {
            Map<String, PluginBase> res = new HashMap<>();
            String[] list = new File(RunPath + "plugins/").list();
            if (list != null) {
                for (String s : list) {
                    getPlugin = s;
                    String[] strArray = s.split("\\.");
                    int suffixIndex = strArray.length - 1;
                    String jarFix = strArray[suffixIndex];
                    if (jarFix.equals("jar")) {
                        try {
                            File pluginFile = new File(RunPath + "plugins/"+s);
                            String hashCode = classLoaderManager.createURLClassLoader(pluginFile);
                            URLClassLoader urlClassLoader = (URLClassLoader) classLoaderManager.getClassLoader(hashCode);
                            JarFile pluginJar = new JarFile(RunPath + "plugins/" + s);

                            Set<String> set = Unit.getClassesFromJar(pluginJar);
                            Description description = getDescription(pluginJar);
                            String lang = Server.getInstance().Language;
                            LangBase langBase = null;
                            //当存在语言文件时
                            if (description.getLanguage()!= null){
                                JarEntry jarEntry = pluginJar.getJarEntry("language/"+lang+".ini");
                                if (jarEntry == null){
                                    JarEntry defaultLang = pluginJar.getJarEntry("language/"+description.getLanguage()+".ini");
                                    if (defaultLang != null){
                                        langBase = new LangBase(pluginJar.getInputStream(defaultLang));
                                    }
                                }else {
                                    langBase = new LangBase(pluginJar.getInputStream(jarEntry));
                                }
                            }
                            if (description == null) {
                                Log.sendWarn("没有配置文件");
                                continue;
                            }
                            String mainClass = description.getMain();
                            Class<PluginBase> pluginClass = (Class<PluginBase>) urlClassLoader.loadClass(mainClass);
                           // Log.sendLog(loadClass.getResource("/").toString());

                            PluginBase plugin = pluginClass.getConstructor().newInstance();
                            if (langBase!=null){
                                plugin.setPluginLang(langBase);
                            }
                            plugin.setDescription(description);
                            plugin.allClazz = set;
                            plugin.classLoaderHashCode = hashCode;
                            plugin.setPluginName(description.getName());
                            res.put(description.getName(), plugin);
                           // Log.sendLog(set.toString());
                            // plugin.onLoad();
                        } catch (IOException e) {
                            Log.sendWarn(Server.getInstance().TranslateOne("Plugin.JarCanNotOpen", s));
                        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                                 InstantiationException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                return res;
            }

        }
        return null;
    }

    @Override
    public Description getDescription() {
        return description;
    }

    @Override
    public String getName() {
        return PluginName;
    }


}

package cn.devspace.nucleus.Plugin;

import cn.devspace.nucleus.Lang.LangBase;
import cn.devspace.nucleus.Manager.ClassLoaderManager;
import cn.devspace.nucleus.Manager.ClassManager;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Server.Server;
import org.apache.tomcat.Jar;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
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
                            plugin.PluginName = description.getName();
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

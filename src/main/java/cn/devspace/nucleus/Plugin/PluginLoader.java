package cn.devspace.nucleus.Plugin;

import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Server.Server;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


import static cn.devspace.nucleus.Server.Server.RunPath;

public class PluginLoader implements Loader {


    protected Description description;

    protected static String LoadingApp;

    protected static String getPlugin;

    protected String PluginName;

    public PluginLoader(Server server, String AppName) {
        // this.AppName = AppName;
        //this.description = loadDescription();
    }


    public Description loadDescription() {
        try {
            return new Description(new ClassPathResource("app/" + this.PluginName + "/app.yml").getInputStream());
        } catch (IOException ioe) {
            Log.sendError(ioe.toString(), 2);
            return null;
        }
    }

    public Map<String, JarFile> getPluginJars() {
        Map<String, JarFile> res = new HashMap<>();
        String[] list = new File(RunPath + "plugins/").list();
        if (list != null) {
            for (String s : list) {
                try {
                    JarFile jarFile = new JarFile(new File(RunPath + "plugins/" + s));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
        return res;
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
                            PluginClassLoader pcl = new PluginClassLoader(this, this.getClass().getClassLoader(), new File(RunPath + "plugins/" + s));
                            Description description = getDescription(new JarFile(RunPath + "plugins/" + s));

                            if (description == null) {
                                Log.sendWarn("没有配置文件");
                                continue;
                            }

                            String mainClass = description.getMain();
                            Class loadClass = pcl.loadClass(mainClass);
                            Class<PluginBase> pluginClass = (Class<PluginBase>) loadClass.asSubclass(PluginBase.class);
                            PluginBase plugin = pluginClass.getConstructor().newInstance();
                            plugin.setDescription(description);
                            plugin.PluginName = description.getName();
                            res.put(description.getName(), plugin);
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

package cn.devspace.nucleus.Plugin;

import cn.devspace.nucleus.Manager.AnnotationManager;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Server.Server;
import org.apache.tomcat.Jar;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static cn.devspace.nucleus.Manager.ManagerBase.getSingeYaml;
import static cn.devspace.nucleus.Server.Server.RunPath;

public class PluginLoader implements Loader{


    protected Description description;

    protected static String LoadingApp;

    protected static String getPlugin;

    protected String AppName;

    public PluginLoader(Server server, String AppName) {
       // this.AppName = AppName;
        //this.description = loadDescription();
    }


    public Description loadDescription() {
        try {
            return new Description(new ClassPathResource("app/" + this.AppName + "/app.yml").getInputStream());
        } catch (IOException ioe) {
            Log.sendError(ioe.toString(), 2);
            return null;
        }
    }

    public Map<String,JarFile> getPluginJars() {
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

    public Description getDescription(JarFile pluginJar){
        try{
            JarEntry entry = pluginJar.getJarEntry("nucleus.yml");
            InputStream stream = null;
            stream = pluginJar.getInputStream(entry);
            return new Description(stream);
        }catch (Exception ie){
            Log.sendWarn("无法载入插件描述文件");
        }
        return null;
    }




    public Map<String, PluginBase> getPlugins(){
        if (!new File(RunPath+"plugins/").exists()){
            return null;
        }else {
            Map<String,PluginBase> res = new HashMap<>();
            String[] list = new File(RunPath+"plugins/").list();
            if (list!=null){
                for (String s : list) {
                    getPlugin = s;
                    String[] strArray = s.split("\\.");
                    int suffixIndex = strArray.length - 1;
                    String jarFix = strArray[suffixIndex];
                    if (jarFix.equals("jar")) {
                        try {
                            PluginClassLoader pcl = new PluginClassLoader(this,this.getClass().getClassLoader(),new File(RunPath+"plugins/"+s));
                            Description description = getDescription(new JarFile(RunPath+"plugins/"+s));

                            if (description == null){
                                Log.sendWarn("没有配置文件");
                                continue;
                            }

                            String mainClass = description.getMain();
                            Class loadClass = pcl.loadClass(mainClass);
                            Class<PluginBase> pluginClass = (Class<PluginBase>) loadClass.asSubclass(PluginBase.class);
                            PluginBase plugin = pluginClass.getConstructor().newInstance();
                            res.put(s.replace(".jar", ""), plugin);

                            plugin.onLoad();

                        } catch (IOException e) {
                            Log.sendWarn(Server.getInstance().TranslateOne("Plugin.JarCanNotOpen",s));
                        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                                 InstantiationException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                Log.sendLog(res.toString());
                return res;
            }

        }
        return null;
    }

    @Override
    public Description getDescription() {
        return description;
    }

    public static void loadPlugins(Server server) {
        try {
            Map<String, ArrayList<String>> Maps = getSingeYaml(RunPath + "/resources/nucleus.yml", true);
            if (Maps != null) {
                ArrayList<String> enableApps = Maps.get("EnableApp");
                for (String apps : enableApps) {
                    LoadingApp = apps;
                    Description appDes = new Description(new ClassPathResource("app/" + apps + "/app.yml").getInputStream());
                    String main = appDes.getMain();
                    Class<?> c = Class.forName(main);
                    AppBase app = (AppBase) c.getDeclaredConstructor().newInstance();
                    Map<String, String> maps = AnnotationManager.getRouterAnnotation(c);
                    Server.RouterList.put(apps, maps);
                    app.setDescription(appDes);
                    Log.AppStart(Server.getInstance().Translators("App.Start", apps));
                    app.localApp(apps);
                    //开始执行onload
                    app.onLoad();
                    Log.AppStart(Server.getInstance().Translators("App.Loaded", apps));
                    Server.AppList.put(apps, app);
                }
            }
        } catch (FileNotFoundException fe) {
            Log.sendWarn(Server.getInstance().TranslateOne("App.NotFound", LoadingApp));
            Log.sendWarn(Server.getInstance().TranslateOne("App.Error", LoadingApp));
        } catch (Exception e) {
            Log.sendWarn(e.toString());
            Log.sendWarn(Server.getInstance().TranslateOne("App.Error", LoadingApp));
        }
    }

}

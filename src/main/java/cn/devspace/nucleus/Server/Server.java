package cn.devspace.nucleus.Server;

import cn.devspace.nucleus.App.Console.Console;
import cn.devspace.nucleus.Lang.LangBase;
import cn.devspace.nucleus.Manager.Annotation.version.Nucleus;
import cn.devspace.nucleus.Manager.BeanManager;
import cn.devspace.nucleus.Manager.ClassLoaderManager;
import cn.devspace.nucleus.Manager.Command.CommandBase;
import cn.devspace.nucleus.Manager.DataBase.DataBaseManager;
import cn.devspace.nucleus.Manager.ManagerBase;
import cn.devspace.nucleus.Manager.SettingManager;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Plugin.AppBase;
import cn.devspace.nucleus.Plugin.AppLoader;
import cn.devspace.nucleus.Plugin.PluginBase;
import cn.devspace.nucleus.Plugin.PluginLoader;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Server extends ManagerBase {

    public static final String VERSION = "0.0.2-alpha";
    public static final String AUTHOR = "Pama Lee";

    public static final String NAME = "Nucleus(JAVA)";

    public static String RunPath = System.getProperty("user.dir") + "/";
    public LangBase lang;
    public SettingManager settingManager;

    public String Language;

    public static Server Instance;

    protected static Thread currentThread;

    public static Map<String, AppBase> AppList = new HashMap<>();
    public static Map<String, PluginBase> PluginList = new HashMap<>();
    public static Map<String, Map<Map<String, String>, Class<?>>> RouterList = new HashMap<>();
    public static Map<String, String> PluginRoute = new HashMap<>();
    public static Map<String, Map<CommandBase, Method>> CommandMap = new HashMap<>();

    public static Map<String,String> CommandHelpMessage = new HashMap<>();

    @Resource
    public BeanManager beanManager;

    //类加载器管理者
    @Nucleus("0.0.2-alpha")
    public static ClassLoaderManager classLoaderManager = new ClassLoaderManager();
    //数据库管理者
    @Nucleus("0.0.2-alpha")
    public DataBaseManager dataBaseManager = new DataBaseManager();


    private static final Runtime runtime = Runtime.getRuntime();

    /**
     * 构建服务器
     */
    @Nucleus("0.0.1")
    public Server() {
        init();
        //初始化多语言
        this.lang = new LangBase();
        this.Language = this.lang.getLanguage();
        this.settingManager = new SettingManager();

        currentThread = Thread.currentThread();
        Instance = this;

    }

    /**
     * 服务器第一次启动的初始化
     * 其中执行一些配置文件的操作
     */
    public static void init() {
        if (!new File(RunPath + "resources/").exists()) {
            try {
                InputStream set = new ClassPathResource("nucleus.yml").getInputStream();
                InputStream route = new ClassPathResource("route.yml").getInputStream();
                //Log.sendLog(RunPath);
                boolean newFile =  new File(RunPath + "resources/").mkdirs();
                if (!newFile) Log.sendError("The configuration file is distributable",12);
                Files.copy(set, Path.of(RunPath + "resources/nucleus.yml"));
                Files.copy(route, Path.of(RunPath + "resources/route.yml"));

                Log.sendError("The configuration file is ready, please reopen the program", 200);
            } catch (IOException e) {
                Log.sendError(e.toString(), 1);
            }

        }
    }


    public void Start() {
        beanManager = new BeanManager();
        this.preDIR();
        //服务器开启

        //处理语言
        Log.sendLog(TranslateOne("Language.Use", this.Language));
        Log.sendLog(TranslateOne("App.Name", getName(), getServerVersion()));
        Log.sendLog(getAuthor());
        Log.sendLog(TranslateOne("App.Version", getServerVersion()));
        Log.sendLog(TranslateOne("App.Licence"));

        String classLoaderPluginHash = classLoaderManager.createClassLoader();

        //AppLoader.loadApps(this);
        initApps(false);
        initPlugins(false);
        Log.sendLog(TranslateOne("App.Run.UseMemory", getUsedMemory()));
    }

    public void initPlugins(boolean reload){
        if (reload){
            for (String key: PluginRoute.keySet()){
                if (PluginList.containsKey(PluginRoute.get(key))){
                    PluginRoute.remove(key);
                    initPlugins(true);
                    return;
                }
            }
        }
        PluginLoader pL = new PluginLoader(this, null,classLoaderManager);
        PluginList = pL.getPlugins();
        LoadPlugin();
        EnablePlugin();
    }

    public void initApps(boolean reload){
        if (reload){
            for (String key: PluginRoute.keySet()){
                if (PluginList.containsKey(PluginRoute.get(key))){
                    PluginRoute.remove(key);
                    initApps(true);
                    return;
                }
            }
        }
        AppLoader appLoader = new AppLoader(this);
        AppList = appLoader.getApps(new HashSet<>());
        LoadApp();
        EnablePlugin();
    }

    private void LoadApp() {
        String cApp = null;
        try {
            if (AppList == null){
                return;
            }
            for (String App : AppList.keySet()) {
                cApp = App;
                AppBase appBase = AppList.get(App);
                if (!appBase.getStatus()){
                    appBase.onLoad();
                    appBase.setLoaded();
                }
            }
        }catch (Exception e){
            Log.sendWarn(TranslateOne("App.LoadError",cApp,e.getMessage()+" where->"+e.getStackTrace()[0]));
            disableApp(cApp);
            LoadApp();
        }
    }

    private void EnableApp() {
        String cApp = null;
        try {
            for (String app : AppList.keySet()) {
                cApp = app;
                AppBase appClass = AppList.get(app);
                appClass.onEnable();
            }
        }catch (Exception e){
            Log.sendWarn(TranslateOne("App.EnableError",cApp,e.getMessage()));
            disableApp(cApp);
        }

    }

    public void EnabledApp() {
        String cApp = null;
        try {
            for (String app : AppList.keySet()) {
                cApp = app;
                AppBase appClass = AppList.get(app);
                BeanManager.registerBean(app,appClass.getClass());
                appClass.onEnabled();
            }
        }catch (Exception e){
            Log.sendWarn(TranslateOne("App.EnabledError",cApp,e.getMessage()));
            disableApp(cApp);
        }

    }

    public void LoadPlugin() {
        String cPlugin = null;
        try {
            for (String plugin : PluginList.keySet()) {
                cPlugin = plugin;
                PluginBase pluginBase = PluginList.get(plugin);
               if (!pluginBase.getStatus()){
                   pluginBase.onLoad();
                   pluginBase.setLoaded();
               }
            }
        }catch (Exception e){
            Log.sendWarn(TranslateOne("Plugin.LoadError",cPlugin,e.getMessage()+" where->"+e.getStackTrace()[0]));
            disablePlugin(cPlugin);
            LoadPlugin();
        }

    }

    public void EnablePlugin() {
        String cPlugin = null;
        try {
            for (String plugin : PluginList.keySet()) {
                cPlugin = plugin;
                PluginBase pluginBase = PluginList.get(plugin);
                if (!pluginBase.isEnable()){
                    pluginBase.onEnable();
                    pluginBase.setEnable(true);
                }
            }
        }catch (Exception e){
            Log.sendWarn(TranslateOne("Plugin.EnableError",cPlugin,e.getMessage()));
            disablePlugin(cPlugin);
            EnablePlugin();
        }

    }

    public void EnabledPlugin() {
        String cPlugin = null;
        try {
            for (String plugin : PluginList.keySet()) {
                cPlugin = plugin;
                PluginBase pluginBase = PluginList.get(plugin);
                if (!pluginBase.isEnabled()){

                    Class<?> pluginClass= pluginBase.getClass();
                    BeanManager.registerBean(plugin,pluginClass);
                    pluginBase.onEnabled();
                    pluginBase.setEnabled(true);
                }
            }
        }catch (Exception e){
            Log.sendWarn(TranslateOne("Plugin.EnabledError",cPlugin,e.getMessage()));
            disablePlugin(cPlugin);
            EnabledPlugin();
        }
    }

    public void disablePlugin(String plugin){
        try {
            PluginList.remove(plugin);
            Log.sendWarn(TranslateOne("Plugin.Disable",plugin));
        }catch (Exception e){
            Log.sendWarn(TranslateOne("Disable.Error",plugin));
        }
    }

    public void disableApp(String app){
        try {
            AppList.remove(app);
            Log.sendWarn(TranslateOne("App.Disable",app));
        }catch (Exception e){
            Log.sendWarn(TranslateOne("Disable.Error",app));
        }
    }


    public void preDIR() {
        //检查插件目录
        if (!new File(RunPath + "plugins/").exists()) {
           boolean newPlugin =  new File(RunPath + "plugins/").mkdir();
           if (!newPlugin) Log.sendError("Can not init Server",13);
        }
        if (!new File(RunPath+"Pages/").exists()){
           boolean newPage =  new File(RunPath+"Pages/").mkdir();
            try {
               boolean new404 =  new File(RunPath+"Pages/404.html").createNewFile();
               if (!newPage || !new404) Log.sendError("Can not init Server",14);
            } catch (IOException e) {
                Log.sendWarn("Can not create 404 file");
            }
        }
    }


    public static double getUsedMemory() {
        String res = String.format("%.2f", (double) runtime.totalMemory() / 1024 / 1024 - (double) runtime.freeMemory() / 1024 / 1024);
        return Double.parseDouble(res);
    }

    public static Server getInstance() {
        return Instance;
    }

    public LangBase getLanguages() {
        return this.lang;
    }

    public String getLangSet() {
        return this.Language;
    }

    public static String getServerVersion() {
        return VERSION;
    }

    public static String getAuthor() {
        return AUTHOR;
    }

    public static void Shutdown(int Code) {
        System.exit(Code);
    }

    public static String getName() {
        return NAME;
    }

}

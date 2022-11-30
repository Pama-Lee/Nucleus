package cn.devspace.nucleus.Server;

import cn.devspace.nucleus.Lang.LangBase;
import cn.devspace.nucleus.Manager.BeanManager;
import cn.devspace.nucleus.Manager.Command.CommandBase;
import cn.devspace.nucleus.Manager.ManagerBase;
import cn.devspace.nucleus.Manager.SettingManager;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Plugin.AppBase;
import cn.devspace.nucleus.Plugin.AppLoader;
import cn.devspace.nucleus.Plugin.PluginBase;
import cn.devspace.nucleus.Plugin.PluginLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
@Component("Server")
public class Server extends ManagerBase {

    public static final String VERSION = "0.0.1-alpha";
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

    @Resource
    public BeanManager beanManager;


    private static final Runtime runtime = Runtime.getRuntime();

    public Server() {
        init();
        //初始化多语言
        this.lang = new LangBase();
        this.Language = this.lang.getLanguage();
        this.settingManager = new SettingManager();

        currentThread = Thread.currentThread();
        Instance = this;
    }

    public static void init() {
        if (!new File(RunPath + "resources/").exists()) {
            try {
                InputStream set = new ClassPathResource("nucleus.yml").getInputStream();
                InputStream route = new ClassPathResource("route.yml").getInputStream();
                Log.sendLog(RunPath);
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

        PluginLoader pL = new PluginLoader(this, null);
        PluginList = pL.getPlugins();

        AppLoader.loadApps(this);
        LoadPlugin();

        Log.sendLog(TranslateOne("App.Run.UseMemory", getUsedMemory()));
        EnableApp();
        EnablePlugin();
        //ConsoleManager con = new ConsoleManager();
        Log.sendLog(CommandMap.toString());
    }

    private void EnableApp() {
        for (String app : AppList.keySet()) {
            AppBase appClass = AppList.get(app);
            appClass.onEnable();
        }
    }

    public static void EnabledApp() {
        for (String app : AppList.keySet()) {
            AppBase appClass = AppList.get(app);
            Server.getInstance().beanManager.registerBean(app,appClass.getClass());
            appClass.onEnabled();
        }
    }

    public static void LoadPlugin() {
        for (String plugin : PluginList.keySet()) {
            PluginBase pluginBase = PluginList.get(plugin);
            pluginBase.onLoad();
        }
    }

    public static void EnablePlugin() {
        for (String plugin : PluginList.keySet()) {
            PluginBase pluginBase = PluginList.get(plugin);
            pluginBase.onEnable();
        }
    }

    public static void EnabledPlugin() {
        for (String plugin : PluginList.keySet()) {
            PluginBase pluginBase = PluginList.get(plugin);
            pluginBase.onEnabled();
        }
    }


    public void preDIR() {
        //检查插件目录
        if (!new File(RunPath + "plugins/").exists()) {
           boolean newPlugin =  new File(RunPath + "plugins/").mkdir();
           if (!newPlugin) Log.sendError("Can not init Server",13);
        }
        if (!new File(RunPath+"Pages/").exists()){
           boolean newPage =  new File(RunPath+"plugins/").mkdir();
            try {
               boolean new404 =  new File(RunPath+"plugins/404.html").createNewFile();
               if (!newPage || !new404) Log.sendError("Can not init Server",13);
            } catch (IOException e) {
                Log.sendWarn("无法创建404文件");
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

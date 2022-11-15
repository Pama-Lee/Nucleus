package cn.devspace.nucleus.Plugin;

import cn.devspace.nucleus.Manager.AnnotationManager;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Server.Server;
import org.springframework.core.io.ClassPathResource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import static cn.devspace.nucleus.Manager.ManagerBase.getSingeYaml;
import static cn.devspace.nucleus.Server.Server.RunPath;

public class AppLoader implements Loader {

    protected Description description;

    protected static String LoadingApp;

    protected String AppName;

    public AppLoader(Server server, String AppName) {
        this.AppName = AppName;
        this.description = loadDescription();
    }


    public Description loadDescription() {
        try {
            return new Description(new ClassPathResource("app/" + this.AppName + "/app.yml").getInputStream());
        } catch (IOException ioe) {
            Log.sendError(ioe.toString(), 2);
            return null;
        }
    }

    @Override
    public Description getDescription() {
        return description;
    }

    @Override
    public String getName() {
        return AppName;
    }

    public static void loadApps(Server server) {
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
                    Map<String, Class<?>> maps = AnnotationManager.getRouterAnnotation(c);
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

package cn.devspace.nucleus.Plugin;

import cn.devspace.nucleus.Entity.RouterClazz;
import cn.devspace.nucleus.Manager.AnnotationManager;
import cn.devspace.nucleus.Manager.BeanManager;
import cn.devspace.nucleus.Manager.RouterBase;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Server.Server;
import org.springframework.core.io.ClassPathResource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static cn.devspace.nucleus.Manager.ManagerBase.getSingeYaml;
import static cn.devspace.nucleus.Server.Server.RunPath;

public class AppLoader implements Loader {

    protected Description description;

    protected static String LoadingApp;

    protected String AppName;

    public AppLoader(Server server) {

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

    public Map<String,AppBase> getApps(Set<String> disableApp){
        Map<String, AppBase> Apps = new HashMap<>(100);
        try {
            Map<String, ArrayList<String>> Maps = getSingeYaml(RunPath + "/resources/nucleus.yml", true);
            if (Maps != null) {
                ArrayList<String> enableApps = Maps.get("EnableApp");
                for (String apps : enableApps) {
                    if (disableApp.contains(apps))continue;
                    LoadingApp = apps;
                    Description appDes = new Description(new ClassPathResource("app/" + apps + "/app.yml").getInputStream());
                    String main = appDes.getMain();
                    Class<?> c = Class.forName(main);
                    AppBase app = (AppBase) c.getDeclaredConstructor().newInstance();
                    //Map<Map<String, String>, Class<?>> maps = AnnotationManager.getRouterAnnotation((Class<RouterBase>) c);
                    RouterClazz routerClazz = AnnotationManager.getRouterAnnotation(appDes.getRoute(), c);
                    //Server.RouterList.put(apps, maps);
                    Server.RouterListNew.add(routerClazz);
                    app.setDescription(appDes);
                    Log.AppStart(Server.getInstance().Translators("App.Start", apps));
                    app.localApp(apps);
                    Apps.put(apps,app);
                    Server.AppList.put(apps, app);
                }
                return Apps;
            }
            return null;
        } catch (FileNotFoundException fe) {
            Log.sendWarn(Server.getInstance().TranslateOne("App.NotFound", LoadingApp));
            Log.sendWarn(Server.getInstance().TranslateOne("App.Error", LoadingApp));
            disableApp.add(LoadingApp);
            return getApps(disableApp);
        } catch (Exception e) {
            Log.sendWarn(e.toString());
            Log.sendWarn(Server.getInstance().TranslateOne("App.Error", LoadingApp));
            disableApp.add(LoadingApp);
           return getApps(disableApp);
        }
    }

}

package cn.devspace.nucleus.Plugin;

import cn.devspace.nucleus.Lang.LangBase;
import cn.devspace.nucleus.Manager.AnnotationManager;
import cn.devspace.nucleus.Manager.ManagerBase;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Server.Server;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.Map;

abstract public class PluginBase extends ManagerBase implements Loader {

    protected static LangBase AppLang = null;

    protected Description description;

    protected String PluginName;

    public PluginBase() {

    }

    public void onLoad() {

    }

    public void onEnable() {

    }

    public void onEnabled() {

    }

    protected void initRoute(Class<?> classes) {
        Map<Map<String, String>, Class<?>> maps = AnnotationManager.getRouterAnnotation(classes);
        Server.RouterList.put(getDescription().getRoute(), maps);
        Server.PluginRoute.put(getDescription().getRoute(), PluginName);
    }


    protected void sendLog(String log) {
        Log.sendAppMessage(new Exception().getStackTrace()[1].getClassName(), log);
    }

    protected LangBase loadLanguage() {
        String language = getLanguage();
        try {
            InputStream langStream = new ClassPathResource("app/" + this.PluginName + "/Language/" + language + ".ini").getInputStream();
            LangBase lb = new LangBase(langStream);
            AppLang = lb;
            return lb;
        } catch (Exception e) {
            Log.sendWarn(e.toString());
            disableApp();
            return null;
        }

    }

    public void setDescription(Description des) {
        description = des;
    }

    public Description getDescription() {
        return description;
    }


    protected String Translation(String key, Object... params) {
        return TranslateOne(key, params);
    }

    protected String Translation(String key, String[] param) {
        return TranslateOne(key, param);
    }

    public void localPlugin(String PluginName) {
        this.PluginName = PluginName;
    }

    protected String getLocalPlugin() {
        return this.PluginName;
    }

    //TODO: ??????APP???????????????
    protected void disableApp() {

    }

    @Override
    public String getName() {
        return PluginName;
    }
}


package cn.devspace.nucleus.Plugin;


import cn.devspace.nucleus.Lang.LangBase;
import cn.devspace.nucleus.Manager.AnnotationManager;
import cn.devspace.nucleus.Manager.DataManager;
import cn.devspace.nucleus.Manager.ManagerBase;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Server.Server;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.DigestUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


abstract public class AppBase extends ManagerBase {

    protected String callback = null;
    protected static String LoadingApp = null;
    protected static LangBase AppLang = null;
    protected Description description;
    private boolean isLoaded = false;
    private boolean isEnabled = false;

    protected String AppName;

    private String key;

    public AppBase() {

    }

    public void onLoad() {

    }

    public void onEnable() {

    }

    public void onEnabled() {

    }

    protected void initRoute(Class<?> classes) {
        Map<Map<String, String>, Class<?>> maps = AnnotationManager.getRouterAnnotation(classes);
        if (!Server.RouterList.get(getDescription().getRoute()).isEmpty()){
           // Log.sendLog(Server.RouterList.toString());
            for(Map<String,String> temp:maps.keySet()){
                Server.RouterList.get(getDescription().getRoute()).put(temp,maps.get(temp));
            }
        }else {
            Server.RouterList.put(getDescription().getRoute(),maps);
        }
        Server.PluginRoute.put(getDescription().getRoute(), AppName);
    }

    public String onCall(String route, String method) {
        return null;
    }

    public String onCall(String route, String method, Map<String, String> Request) {
        return null;
    }


    protected DataManager getDataManager() {
        if (description.getDataBase() == null) {
            // TODO: 应当给出提示
            return null;
        } else {
            // return new DataManager();
        }
        return null;
    }


    protected void sendLog(String log) {
        Log.sendAppMessage(new Exception().getStackTrace()[1].getClassName(), log);
    }


    protected LangBase loadLanguage() {
        String language = getLanguage();
        try {
            InputStream langStream = new ClassPathResource("app/" + this.AppName + "/Language/" + language + ".ini").getInputStream();
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

    public void localApp(String AppName) {
        this.AppName = AppName;
        this.key = DigestUtils.md5DigestAsHex((String.valueOf(System.currentTimeMillis())+AppName+Server.getServerVersion()).getBytes());
    }

    public String getKey(){
        return this.key;
    }

    protected String getLocalApp() {
        return this.AppName;
    }

    //TODO: 卸载APP，等待完善
    protected void disableApp() {

    }

    public boolean getStatus(){
        return this.isLoaded;
    }

    public void setLoaded() {
        this.isLoaded = true;
    }

    public void setEnabled() {
        this.isEnabled = true;
    }
    public boolean isEnabled() {
        return this.isEnabled;
    }

}

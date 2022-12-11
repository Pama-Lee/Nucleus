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

    private LangBase PluginLang = null;

    private boolean isLoaded = false;

    private boolean isEnable = false;

    private boolean isEnabled = false;

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
        if (!Server.RouterList.get(getDescription().getRoute()).isEmpty()){
            for(Map<String,String> temp:maps.keySet()){
                Server.RouterList.get(getDescription().getRoute()).put(temp,maps.get(temp));
            }
        }
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
            PluginLang = lb;
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

    public void setPluginLang(LangBase langBase){this.PluginLang = langBase;}

    public Description getDescription() {
        return description;
    }


    protected String translateMessage(String key, Object... params) {
        return Translator(key, params);
    }

    protected String translateMessage(String key, String[] param) {
        return Translator(key, (Object) param);
    }

    private String Translator(String key, Object... params){
        if (this.PluginLang == null){
            Log.sendWarn("Don't exist any language config");
            return null;
        }else{
           return this.PluginLang.TranslateOne(key,params);
        }
    }


    public void localPlugin(String PluginName) {
        this.PluginName = PluginName;
    }

    protected String getLocalPlugin() {
        return this.PluginName;
    }

    //TODO: 卸载APP，等待完善
    protected void disableApp() {

    }

    public void setLoaded(){
        this.isLoaded = true;
    }

    public void setDisable(){
        this.isLoaded = false;
    }

    public boolean getStatus(){
        return this.isLoaded;
    }

    @Override
    public String getName() {
        return PluginName;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}


package cn.devspace.nucleus.Plugin;

import cn.devspace.nucleus.Lang.LangBase;
import cn.devspace.nucleus.Manager.AnnotationManager;
import cn.devspace.nucleus.Manager.ManagerBase;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Server.Server;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

abstract public class PluginBase extends ManagerBase implements Loader {

    private LangBase PluginLang = null;

    public String classLoaderHashCode = null;

    private boolean isLoaded = false;

    private boolean isEnable = false;

    private boolean isEnabled = false;

    protected Description description;

    protected String PluginName;

    private String key;

    public Set<String> allClazz = new HashSet<>();

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
        if (Server.RouterList.get(getDescription().getRoute()) != null){
            for(Map<String,String> temp:maps.keySet()){
                Server.RouterList.get(getDescription().getRoute()).put(temp,maps.get(temp));
            }
        }else {
            Server.RouterList.put(getDescription().getRoute(),maps);
        }
        Server.PluginRoute.put(getDescription().getRoute(), PluginName);
    }


    protected void sendLog(String log) {
        Log.sendAppMessage(new Exception().getStackTrace()[1].getClassName(), log);
    }


    protected LangBase loadLanguage(LangBase langBase) {
        try {
            PluginLang = langBase;
            return langBase;
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

    public LangBase getPluginLang() {
        return PluginLang;
    }

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


    public void setPluginName(String name){
        this.PluginName = name;
        this.key =  DigestUtils.md5DigestAsHex((String.valueOf(System.currentTimeMillis())+name+Server.getServerVersion()).getBytes());
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

    public String getKey(){
        return this.key;
    }

}


package cn.devspace.nucleus.Plugin;

import cn.devspace.nucleus.Entity.Router;
import cn.devspace.nucleus.Entity.RouterClazz;
import cn.devspace.nucleus.Lang.LangBase;
import cn.devspace.nucleus.Manager.AnnotationManager;
import cn.devspace.nucleus.Manager.ManagerBase;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Server.Server;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.InputStream;
import java.util.*;

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
        RouterClazz routerClazz = AnnotationManager.getRouterAnnotation(getDescription().getRoute(), classes);

        boolean isExist = false;
        // 查询是否已经存在路由
        for (RouterClazz temp : Server.RouterListNew) {
            if (temp.getRouteName().equals(routerClazz.getRouteName())) {
                // 合并List
                List<Router> newRouters = new ArrayList<>();
                newRouters.addAll(temp.getRouters());
                newRouters.addAll(routerClazz.getRouters());
                temp.setRouters(newRouters);
                isExist = true;
            }
        }
        if (!isExist) {
            Server.RouterListNew.add(routerClazz);
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

    public String getPluginDataPath(){
        // 替换\为/
        String s = Server.RunPath+"PluginData"+File.separator+this.PluginName+File.separator;
        // 检查目录是否存在
File file = new File(s);
if (file.exists() && file.isDirectory()) {
        return s;
    }else {
        file.mkdirs();
        return s;
    }
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


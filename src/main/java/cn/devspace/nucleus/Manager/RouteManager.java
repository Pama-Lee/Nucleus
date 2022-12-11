package cn.devspace.nucleus.Manager;


import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Plugin.Loader;
import cn.devspace.nucleus.Server.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * 主路由
 */
public class RouteManager extends ManagerBase {

    protected static Map<String, ArrayList<String>> ALLOW_ROUTE = new HashMap<>();

    public RouteManager() {
        ALLOW_ROUTE = loadRoute();
    }

    public Map<String, ArrayList<String>> loadRoute() {
        try {
            return getSingeYaml(System.getProperty("user.dir") + "/resources/route.yml", true);
        } catch (Exception fn) {
            Log.sendError(fn.toString(), 3);
            return null;
        }
    }

    public Map<String, ArrayList<String>> getRouteMap() {
        return ALLOW_ROUTE;
    }


    public static void registerRouter(Loader loader, String routeName, Class<RouterBase> classes) {
        Map<Map<String, String>, Class<?>> maps = AnnotationManager.getRouterAnnotation(classes);
        Server.RouterList.put(routeName, maps);
    }

    public String getLanguage() {
        return new SettingManager().getSetting("app.language");
    }

    /**
     * 判断传入的参数是否包含所需参数
     * @param maps 传入参数
     * @param params 需要包含的参数
     * @return 返回正确与否
     */
    public boolean checkParams(Map<String,String> maps, String[] params){
        for (String param : params) {
            if(!maps.containsKey(param)){
                return false;
            }
        }
        return true;
    }


}

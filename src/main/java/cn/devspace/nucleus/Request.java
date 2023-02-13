package cn.devspace.nucleus;

import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Plugin.AppBase;
import cn.devspace.nucleus.Server.Server;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@RestController
@CrossOrigin
public class Request extends HttpServlet {
    @GetMapping("/App/**")
    public Object Router(HttpServletRequest request) {
        Map<String, Map<Map<String, String>, Class<?>>> router = Server.RouterList;
        Set<String> map = router.keySet();
       // Log.sendLog(map.toString());
        for (String app : map) {
            for (Map<String, String> method : router.get(app).keySet()) {
               // Log.sendLog(router.get(app).keySet().toString());
                Map<Map<String, String>, Class<?>> AppRouters = router.get(app);
                String ReqURI = request.getRequestURI();
                // Log.sendLog(ReqURI);
                 //Log.sendLog("/App/" + app + "/" + method.get("R"));
                if (ReqURI.equals("/App/" + app + "/" + method.get("R"))) {
                    try {
                        AppBase ab = Server.AppList.get(app);
                        String pb = Server.PluginRoute.get(app);
                        // Log.sendLog(AppRouters.toString());
                        if (ab != null) {
                            return toRoute(method, AppRouters,null);
                        }
                        if (pb != null) {
                            app = Server.PluginRoute.get(app);
                            return toRoute(method, AppRouters,null);
                        }
                    } catch (Exception e){
                        Log.sendWarn(e.getStackTrace()[0].toString());
                    }
                }
            }
        }
        return "404";
    }

    @PostMapping("/App/**")
    public Object Router(@RequestBody Map<String,String> params,HttpServletRequest httpServletRequest){
        Map<String, Map<Map<String, String>, Class<?>>> router = Server.RouterList;
        Set<String> map = router.keySet();
        for (String app : map) {
            for (Map<String, String> method : router.get(app).keySet()) {
                Map<Map<String, String>, Class<?>> AppRouters = router.get(app);
                String ReqURI = httpServletRequest.getRequestURI();
                 StringBuilder sb = new StringBuilder();
                 sb.append("/App/").append(app).append("/").append(method.get("R"));
                 String url = sb.toString();
                if (ReqURI.equals(url)) {
                    Log.sendLog("匹配");
                    try {
                        AppBase ab = Server.AppList.get(app);
                        String pb = Server.PluginRoute.get(app);
                        Map<String,String> json = Server.PluginRoute;
                        // Log.sendLog(AppRouters.toString());
                        if (ab != null) {
                            return toRoute(method, AppRouters,params);
                        }
                        if (pb != null) {
                            app = Server.PluginRoute.get(app);
                            return toRoute(method, AppRouters,params);
                        }
                        Log.sendLog(app);
                        Log.sendLog("匹配不到方法");
                    } catch (Exception e){
                        Log.sendWarn(e.toString());
                    }
                }
            }
        }
        return "404";
    }


    private Object toRoute(Map<String, String> method, Map<Map<String, String>, Class<?>> appRouters,Map<String,String> json) {
        //当为GET请求时
        if (json == null){
            json = new HashMap<>();
        }
        try {
            Method methods = appRouters.get(method).getMethod(method.get("M"), Map.class);
            Object m = methods.invoke(appRouters.get(method).getConstructor().newInstance(), json);
            if (m != null) {
                return m;
            } else {
                return null;
            }
        }catch (InvocationTargetException ite){
            Log.sendWarn(method.get("R")+"路由出现错误");
            Log.sendWarn(ite.getTargetException().getMessage());
        } catch (Exception e){
            Log.sendWarn(method.get("R")+"路由出现错误");
            e.printStackTrace();
        }
       return null;
    }
}

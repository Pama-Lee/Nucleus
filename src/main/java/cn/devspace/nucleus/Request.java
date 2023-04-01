package cn.devspace.nucleus;

import cn.devspace.nucleus.App.VisitLobby.Entity.Visit;
import cn.devspace.nucleus.App.VisitLobby.Main;
import cn.devspace.nucleus.Entity.Router;
import cn.devspace.nucleus.Entity.RouterClazz;
import cn.devspace.nucleus.Entity.UploadRouter;
import cn.devspace.nucleus.Manager.Annotation.version.Nucleus;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Plugin.AppBase;
import cn.devspace.nucleus.Server.Server;
import cn.devspace.nucleus.Units.Unit;
import com.google.gson.Gson;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@RestController
@CrossOrigin
public class Request extends HttpServlet {
    @GetMapping("/App/**")
    public Object GetRouter(@RequestParam(required = false) Map<String, String> params, HttpServletRequest request) {
        List<RouterClazz> router = Server.RouterListNew;
        Set<String> map = Server.PluginRoute.keySet();
        for(RouterClazz routerClazz : router){
            for (Router router1 : routerClazz.getRouters()){
                String url = "/App/" + routerClazz.getRouteName() + "/" + router1.getURL();
                // 成功匹配
                if (request.getRequestURI().equals(url)){
                    String app = Server.PluginRoute.get(routerClazz.getRouteName());
//                    Log.sendLog(request.getRequestURI());
//                    Log.sendLog(url);
//                    Log.sendLog("匹配");
                    try {
                        AppBase ab = Server.AppList.get(app);
                        String pb = Server.PluginRoute.get(routerClazz.getRouteName());
                        // Log.sendLog(AppRouters.toString());
                        if (ab != null) {
                            return toRoute(router1,params);
                        }
                        if (pb != null) {
                            app = Server.PluginRoute.get(app);
                            return toRoute(router1,params);
                        }
                        Log.sendWarn("未知错误");
                    } catch (Exception e){
                        Log.sendWarn(e.getStackTrace()[0].toString());
                    }
                }
            }
        }
        return "404";
    }

    // 可同时接收文件和参数
    @PostMapping(path = "/App/**", produces = {MediaType.APPLICATION_JSON_VALUE,  MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @CrossOrigin(origins = "*", maxAge = 3600)
    public Object Router(@RequestBody Map<String, Object> params
            , HttpServletRequest httpServletRequest){
        List<RouterClazz> router = Server.RouterListNew;
        for(RouterClazz routerClazz : router){
            for (Router router1 : routerClazz.getRouters()){
                String url = "/App/" + routerClazz.getRouteName() + "/" + router1.getURL();
                // 成功匹配
                if (httpServletRequest.getRequestURI().equals(url)){
                    String app = Server.PluginRoute.get(routerClazz.getRouteName());

                    try {
                        AppBase ab = Server.AppList.get(app);
                        String pb = Server.PluginRoute.get(routerClazz.getRouteName());

                        if (ab != null) {
                            // 如果params是Map<String, String>类型的话
                            if (isStringMap(params))
                            {
                                Map<String, String> newParams = new HashMap<>();
                                for (String key : params.keySet()) {
                                    // 去除null值
                                    if (params.get(key) == null) {
                                        continue;
                                    }
                                    newParams.put(key, params.get(key).toString());
                                }
                                return toRoute(router1,newParams);
                            }else {
                                return toRouteObject(router1,params);
                            }
                        }
                        if (pb != null) {
                            // 如果params是Map<String, String>类型的话
                            if (isStringMap(params))
                            {
                                Map<String, String> newParams = new HashMap<>();
                                for (String key : params.keySet()) {
                                    if (params.get(key) == null) {
                                        continue;
                                    }
                                    newParams.put(key, params.get(key).toString());
                                }
                                return toRoute(router1,newParams);
                            }else {
                                app = Server.PluginRoute.get(app);
                                return toRouteObject(router1,params);
                            }

                        }
                    } catch (Exception e){
                        Log.sendWarn(e.toString());
                    }


                }
            }
        }
        return "404";
    }

    /**
     * 单独给出文件上传的接口
     * @param file
     * @param params
     * @param httpServletRequest
     * @return
     */
    @Nucleus(version = "0.0.4")
    @PostMapping(path = "/Upload/App/**")
    public Object Router(@RequestPart(required = false) MultipartFile file,
                         @RequestParam Map<String, String> params,
                         HttpServletRequest httpServletRequest) {
        Map<String , UploadRouter> uploadRouterMap = Server.UploadRouterList;
        Set<String> map = uploadRouterMap.keySet();
        for (String url : map){
            if (httpServletRequest.getRequestURI().equals("/Upload/App/"+url)){
                UploadRouter uploadRouter = uploadRouterMap.get(url);
                try {
                    return toRouteFile(uploadRouter,file,params);
                } catch (Exception e){
                    Log.sendWarn(e.getStackTrace()[0].toString());
                }
            }
        }
        return null;

    }


    private Object toRoute(Router appRouters,Map<String,String> json) {
        try {
            Method methods = appRouters.getClazz().getMethod(appRouters.getMethod(), Map.class);
            Object m = methods.invoke(appRouters.getClazz().getConstructor().newInstance(), json);
            if (m != null) {
                return m;
            } else {
                return null;
            }
        }catch (InvocationTargetException ite){
            Log.sendWarn(appRouters.getURL()+"路由出现错误");
            Log.sendWarn(ite.getTargetException().getMessage());
        } catch (Exception e){
            Log.sendWarn(appRouters.getURL()+"路由出现错误");
            e.printStackTrace();
        }
       return null;
    }

    @Nucleus(version = "0.0.4")
    private Object toRouteObject(Router appRouters,Map<String,Object> json) {
        //当为GET请求时
        if (json == null){
            json = new HashMap<>();
        }
        try {
            Method methods = appRouters.getClazz().getMethod(appRouters.getMethod(), Map.class);
            Object m = methods.invoke(appRouters.getClazz().getConstructor().newInstance(), json);
            if (m != null) {
                return m;
            } else {
                return null;
            }
        }catch (InvocationTargetException ite){
            Log.sendWarn(appRouters.getURL()+"路由出现错误");
            Log.sendWarn(ite.getTargetException().getMessage());
        } catch (Exception e){
            Log.sendWarn(appRouters.getURL()+"路由出现错误");
            e.printStackTrace();
        }
        return null;
    }

    @Nucleus(version = "0.0.4")
    private Object toRouteFile(UploadRouter uploadRouter, MultipartFile file, Map<String, String> params){
        try {
            Method methods = uploadRouter.getClazz().getMethod(uploadRouter.getMethod(), Map.class,MultipartFile.class);
            Object m = methods.invoke(uploadRouter.getClazz().getConstructor().newInstance(), params, file);
            if (m != null) {
                return m;
            } else {
                return null;
            }
        }catch (InvocationTargetException ite){
            Log.sendWarn(uploadRouter.getURL()+"路由出现错误");
            Log.sendWarn(ite.getTargetException().getMessage());
        } catch (Exception e){
            Log.sendWarn(uploadRouter.getURL()+"路由出现错误");
            e.printStackTrace();
        }
        return null;
    }







    public static boolean isStringMap(Map<String, Object> map) {
        for (Object value : map.values()) {
            if (value == null) {
                continue;
            }
            if (!(value instanceof String) && !(value instanceof Integer ) && !(value instanceof Long) && !(value instanceof Double) && !(value instanceof Float) && !(value instanceof Boolean)) {
                return false;
            }
        }
        return true;
    }


}

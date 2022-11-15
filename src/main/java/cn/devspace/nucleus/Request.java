package cn.devspace.nucleus;

import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Plugin.AppBase;
import cn.devspace.nucleus.Plugin.PluginBase;
import cn.devspace.nucleus.Server.Server;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;


@RestController
public class Request extends HttpServlet {
    @GetMapping("**")
    public String Router(HttpServletRequest request) {
        Map<String, Map<String, Class<?>>> router = Server.RouterList;
        Set<String> map = router.keySet();
        for (String app : map) {
            for (String method : router.get(app).keySet()) {
                Map<String, Class<?>> AppRouters = router.get(app);
                String ReqURI = request.getRequestURI();
                if (ReqURI.equals("/" + app + "/" + method)) {
                    try {
                        AppBase ab = Server.AppList.get(app);
                        String pb = Server.PluginRoute.get(app);
                        if (ab != null){
                            return toRoute(method, AppRouters);
                        }
                        if (pb != null){
                            app = Server.PluginRoute.get(app);
                            return toRoute(method, AppRouters);
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        Log.sendWarn(e.toString());
                    } catch (NoSuchMethodException | InstantiationException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return "404";
    }

    private String toRoute(String method, Map<String, Class<?>> appRouters) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Method methods = appRouters.get(method).getMethod(method);
        Object doMethod = appRouters.get(method).getConstructor().newInstance();
        Object m = methods.invoke(doMethod);
        if (m != null) {
            return m.toString();
        } else {
            return null;
        }
    }

/*
    @GetMapping("/{route}/{method}")
    public String Requests_GET(@PathVariable("route") String route, @PathVariable("method") String method,@RequestParam Map<String, String> map) {
        if (!map.isEmpty()){
            return new Router(route,method).start(route,method,map);
        }
        return new Router(route, method).start(route, method);
    }

    @PostMapping("/{route}/{method}")
    public String Requests_POST(@PathVariable("route") String route, @PathVariable("method") String method,@RequestBody Map<String, String> map) {
        if (map != null){
            return new Router(route,method).start(route,method,map);
        }
        return new Router(route, method).start(route, method);
    }
    @GetMapping("/")
    public String index(){
        return "<h1 style='color:orange'>Welcome to Use nucleus!</h1>";
    }



*/
}

package cn.devspace.nucleus.Manager;

import cn.devspace.nucleus.Entity.RouterClazz;
import cn.devspace.nucleus.Entity.UploadRouter;
import cn.devspace.nucleus.Manager.Annotation.Commands;
import cn.devspace.nucleus.Manager.Annotation.Router;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Server.Server;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 注解管理器
 * 为令内置源码避免使用springboot原始注解
 */
public class AnnotationManager {

    public static RouterClazz getRouterAnnotation(String appName ,Class<?> classes) {
        Method[] methods = classes.getMethods();
        Map<Map<String, String>, Class<?>> res = new HashMap<>();
        RouterClazz routerClazz = new RouterClazz();
        routerClazz.setRouteName(appName);
        for (Method method : methods) {
            Router router = method.getAnnotation(Router.class);
            // 只有isUpload为false时才能使用
            if (router != null && !router.isUpload()){
                cn.devspace.nucleus.Entity.Router router1 = new cn.devspace.nucleus.Entity.Router();
                router1.setURL(router.value());
                router1.setMethod(method.getName());
                router1.setClazz(classes);
                router1.setAppName(appName);
                routerClazz.getRouters().add(router1);
            }
            if (router != null && router.isUpload()){
                UploadRouter uploadRouter = new UploadRouter();
                uploadRouter.setURL(router.URI());
                uploadRouter.setMethod(method.getName());
                uploadRouter.setClazz(classes);
                uploadRouter.setAppName(appName);
                Server.UploadRouterList.put(appName+'/'+router.URI(), uploadRouter);
            }
        }
        return routerClazz;
    }

    public static Map<String, String> getCommandsAnnotation(Class<?> classes) {
        Method[] methods = classes.getMethods();
        Map<String, String> res = new HashMap<>();
        for (Method method : methods) {
            Commands commands = method.getAnnotation(Commands.class);
            if (commands != null) {
                res.put(method.getName(), commands.Command());
            }
        }
        return res;
    }
    public static Map<String, String> getCommandsHelpMessage(Class<?> classes) {
        Method[] methods = classes.getMethods();
        Map<String, String> res = new HashMap<>();
        for (Method method : methods) {
            Commands commands = method.getAnnotation(Commands.class);
            if (commands != null) {
                res.put(commands.Command(), commands.help());
            }
        }
        return res;
    }

}

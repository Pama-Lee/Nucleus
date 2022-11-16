package cn.devspace.nucleus.Manager;

import cn.devspace.nucleus.Manager.Annotation.Commands;
import cn.devspace.nucleus.Manager.Annotation.Router;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 注解管理器
 * 为令内置源码避免使用springboot原始注解
 */
public class AnnotationManager {

    public static Map<Map<String, String>, Class<?>> getRouterAnnotation(Class<?> classes) {
        Method[] methods = classes.getMethods();
        Map<Map<String, String>, Class<?>> res = new HashMap<>();
        for (Method method : methods) {
            Router router = method.getAnnotation(Router.class);
            if (router != null) {
                Map<String, String> rel = new HashMap<>();
                rel.put("M", method.getName());
                rel.put("R", router.value());
                res.put(rel, classes);
            }
        }
        return res;
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

}

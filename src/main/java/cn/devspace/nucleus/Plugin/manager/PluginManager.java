package cn.devspace.nucleus.Plugin.manager;

import cn.devspace.nucleus.Manager.Annotation.PluginTransfer;
import cn.devspace.nucleus.Manager.Annotation.version.Nucleus;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Plugin.PluginBase;
import cn.devspace.nucleus.Server.Server;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 这个类用于提供插件之间的通信
 */
@Nucleus(version = "0.0.5")
public class PluginManager {

    /**
     * 调用插件的方法
     * 目前只支持在加载完毕所有插件后调用
     */
    public PluginDataTransfer invoke(String packageName, String className, String methodName, Map<String , Object> args) {
        // 获取PluginList
        Map<String, PluginBase> pluginBaseMap = Server.PluginList;
        for (String key : pluginBaseMap.keySet()) {
            PluginBase pluginBase = pluginBaseMap.get(key);
            if (pluginBase.getDescription().getPackageName() == null) {
                continue;
            }
            if (pluginBase.getDescription().getPackageName().equals(packageName)) {
                // 找到了插件
                // 获取插件的类
                // 拼接类名
                String fullClassName = packageName + "." + className;
                try {
                    Class<?> pluginClass = pluginBase.getClass().getClassLoader().loadClass(fullClassName);
                    // 获取方法
                    Method[] methods = pluginClass.getDeclaredMethods();
                    for (Method method : methods) {
                        if (method.getName().equals(methodName)) {
                            //必须包含PluginTransfer注释
                            if (!method.isAnnotationPresent(PluginTransfer.class)) {
                                continue;
                            }
                            // 找到了方法
                            PluginDataTransfer pluginDataTransfer = new PluginDataTransfer();
                            pluginDataTransfer.pluginName = pluginBase.getDescription().getName();
                            // 获取调用了这个方法的类名
                            String sourceClassName = new Exception().getStackTrace()[1].getClassName();
                            String sourceMethodName = new Exception().getStackTrace()[1].getMethodName();

                            pluginDataTransfer.methodName = sourceMethodName;
                            pluginDataTransfer.className = sourceClassName;
                            pluginDataTransfer.args = args;
                            // 调用方法
                            Object res = method.invoke(pluginClass.getConstructor().newInstance(), pluginDataTransfer);
                            if (res instanceof PluginDataTransfer) {
                                return (PluginDataTransfer) res;
                            } else {
                                return new PluginDataTransfer("Target Method has no return value");
                            }
                        }
                    }
                    // 找不到方法
                    return new PluginDataTransfer("Method ["+methodName+"] Not Found");

                } catch (Exception e) {
                    return new PluginDataTransfer("Target Method has an error:"+e.getMessage());
                }
            }
        }

        // 没有找到插件
        return new PluginDataTransfer("Plugin Not Found");

    }

    /**
     * 通过反射获取对象的属性和值
     * @param obj
     * @return
     */
    public static Map<String, Object> createTable(Object obj) {
        Map<String, Object> table = new HashMap<>();

        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(obj);
                table.put(field.getName(), value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("get")) {
                try {
                    Object value = method.invoke(obj);
                    String propName = method.getName().substring(3);
                    propName = propName.substring(0, 1).toLowerCase() + propName.substring(1);
                    table.put(propName, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return table;
    }

}

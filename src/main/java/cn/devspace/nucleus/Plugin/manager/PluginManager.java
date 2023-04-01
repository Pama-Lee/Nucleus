package cn.devspace.nucleus.Plugin.manager;

import cn.devspace.nucleus.Manager.Annotation.PluginTransfer;
import cn.devspace.nucleus.Manager.Annotation.version.Nucleus;
import cn.devspace.nucleus.Plugin.PluginBase;
import cn.devspace.nucleus.Server.Server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    public PluginDataTransfer invoke(String packageName, String className, String methodName, Object[] args) {
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
                                return new PluginDataTransfer("Target Method has no PluginTransfer Annotation");
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

                } catch (Exception e) {
                    return new PluginDataTransfer("Class Not Found");
                }
            }
        }

        // 没有找到插件
        return new PluginDataTransfer("Plugin Not Found");

    }

}

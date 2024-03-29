/*
 *
 *  _   _            _
 * | \ | |          | |
 * |  \| |_   _  ___| | ___ _   _ ___
 * | . ` | | | |/ __| |/ _ \ | | / __|
 * | |\  | |_| | (__| |  __/ |_| \__ \
 * \_| \_/\__,_|\___|_|\___|\__,_|___/
 * Author: Pama Lee
 * CT: 2022/11/30 下午12:34
 */

package cn.devspace.nucleus.Manager;

import cn.devspace.nucleus.Manager.Annotation.version.Nucleus;
import cn.devspace.nucleus.Manager.ClassLoader.DevClassLoader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class ClassLoaderManager {
    //类加载器哈希表
    private Map<String,ClassLoader> classLoaderMap = new HashMap<>();

    private Map<String,String> classLoaderMapping = new HashMap<>();
    //初始化工厂方法
    private ClassLoaderFactory classLoaderFactory = new ClassLoaderFactory();

    public Map<String, String> getClassLoaderMapping() {
        return classLoaderMapping;
    }

    public Map<String, ClassLoader> getClassLoaderMap() {
        return classLoaderMap;
    }

    /**
     * 创建默认类加载器
     *
     * @return 返回classloader对应的哈希值, 可通过哈希值拿到加载器
     */
    public String createClassLoader(){
        ClassLoader classLoader =  classLoaderFactory.createClassLoaderTemplate();
        return putLoaderMap(classLoader);
    }

    /**
     * 创建默认URL构造器
     * @param file 需要构造的class
     * @return 返回classloader对应的哈希值, 可通过哈希值拿到加载器
     */
    @Nucleus("v0.0.2-alpha")
    public String createURLClassLoader(File file) throws MalformedURLException {
        URLClassLoader urlClassLoader = classLoaderFactory.createURLClassLoaderTemplate(file);
        return  putLoaderMap(urlClassLoader);
    }

    public String createDevClassLoader(String pluginPath) throws MalformedURLException {
        DevClassLoader devClassLoader = classLoaderFactory.createDevClassLoaderTemplate(pluginPath);
        return  putLoaderMap(devClassLoader);
    }

    /**
     * 直接通过类加载器的哈希值拿到类加载器
     * @param hashCode 哈希值
     * @return 返回找到的类加载器
     */
    public ClassLoader getClassLoader(String hashCode){
        return classLoaderMap.get(hashCode);
    }



    /**
     *
     * @param ClassLoaderName classloader.getName()
     * @param useName 是否使用加载器名拿去类加载器
     * @return 返回翻去到的类加载器
     */
    public ClassLoader getClassLoader(String ClassLoaderName,boolean useName){
        if (classLoaderMapping.get(ClassLoaderName) != null){
            return classLoaderMap.get(classLoaderMapping.get(ClassLoaderName));
        }
        return null;
    }

    /**
     * 将类加载器放进表
     * @param classLoader 类加载器
     * @return 返回存储对应的哈希索引
     */
    public String putLoaderMap(ClassLoader classLoader){
        String hashCode = String.valueOf(classLoader.hashCode());
        classLoaderMapping.put(classLoader.getName(),hashCode);
        classLoaderMap.put(hashCode,classLoader);
        return hashCode;
    }




}

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

import java.util.HashMap;
import java.util.Map;

public class ClassLoaderManager {
    //
    private Map<String,ClassLoader> classLoaderMap = new HashMap<>();
    //初始化工厂方法
    private ClassLoaderFactory classLoaderFactory = new ClassLoaderFactory();

    //创建默认类加载器
    public ClassLoader createClassLoader(){
        return classLoaderFactory.createClassLoaderTemplate();
    }



}

/*
 *
 *  _   _            _
 * | \ | |          | |
 * |  \| |_   _  ___| | ___ _   _ ___
 * | . ` | | | |/ __| |/ _ \ | | / __|
 * | |\  | |_| | (__| |  __/ |_| \__ \
 * \_| \_/\__,_|\___|_|\___|\__,_|___/
 * Author: Pama Lee
 * CT: 2022/11/30 下午12:36
 */

package cn.devspace.nucleus.Manager;

import cn.devspace.nucleus.Manager.Annotation.version.Nucleus;
import cn.devspace.nucleus.Manager.ClassLoader.PluginClassLoader;
import cn.devspace.nucleus.Manager.ClassLoader.defaultClassLoader;
import cn.devspace.nucleus.Server.Server;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URLClassLoader;

//classloader工厂方法
public class ClassLoaderFactory {


    public ClassLoader createClassLoaderTemplate(){
     return new defaultClassLoader();
    }

    @Nucleus("v0.0.2-alpha")
    public URLClassLoader createURLClassLoaderTemplate(File file) throws MalformedURLException {
        //使用第一次创建的Server中的构造器
        return new PluginClassLoader(Server.getInstance().getClass().getClassLoader(), file);
    }


}

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

import cn.devspace.nucleus.Manager.ClassLoader.defaultClassLoader;

//classloader工厂方法
public class ClassLoaderFactory {


    public ClassLoader createClassLoaderTemplate(){
     return new defaultClassLoader();
    }


}

package cn.devspace.nucleus.Manager;

import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Plugin.Description;
import cn.devspace.nucleus.Plugin.Loader;
import cn.devspace.nucleus.Plugin.PluginClassLoader;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static cn.devspace.nucleus.Server.Server.RunPath;

public class ClassManager implements Loader {
    public Set<Class<?>> getClasses(String pack, JarFile jarFile,String s) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        String packageName = pack;
        String packageDirName = packageName.replace('.', '/' );
                    JarFile jar = jarFile;
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        if (name.charAt(0) == '/' ) {
                            name = name.substring(1 );
                        }
                        if (name.startsWith(packageDirName)) {
                            int idx = name.lastIndexOf('/' );
                            if (idx != -1 ) {
                                packageName = name.substring(0, idx).replace('/', '.' );
                            }
                            if ((idx != -1)) {
                                if (name.endsWith(".class") && ! entry.isDirectory()) {
                                    //去掉后面的".class" 获取真正的类名
                                    String className = name.substring(packageName.length() + 1, name.length() - 6);
                                    try {
                                        PluginClassLoader pcl = new PluginClassLoader(this,this.getClass().getClassLoader(), new File(RunPath + "plugins/" + s));
                                        Class<?> loadClass = pcl.loadClass(packageName + '.' + className);
                                        classes.add(loadClass);
                                    } catch (ClassNotFoundException | MalformedURLException e) {
                                        e.printStackTrace();
                                    } catch (Exception e){
                                        Log.sendLog(e.toString());
                                    }
                                }
                            }
                        }
                    }
        return classes;
    }

    @Override
    public Description getDescription() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}

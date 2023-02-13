package cn.devspace.nucleus.Manager.ClassLoader;

import cn.devspace.nucleus.Plugin.Loader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class PluginClassLoader extends URLClassLoader {

    private final Map<String, Class> classes = new HashMap<>();

    public PluginClassLoader(ClassLoader parent, File file) throws MalformedURLException {
        super(new URL[]{file.toURI().toURL()}, parent);
    }

    public PluginClassLoader(ClassLoader parent, URL[] file) throws MalformedURLException {
        super(file, parent);
    }
}

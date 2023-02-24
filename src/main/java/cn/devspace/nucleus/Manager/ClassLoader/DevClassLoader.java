package cn.devspace.nucleus.Manager.ClassLoader;
import cn.devspace.nucleus.Message.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
public class DevClassLoader extends ClassLoader {

        private String dir;

        public DevClassLoader(String dir) {
            this.dir = dir;
        }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        String classFileName = name.replace(".", File.separator) + ".class";
        // 只需要最后的文件名
        String c = classFileName.substring(classFileName.lastIndexOf(File.separator) + 1);
        File classFile = findFile(new File(dir), c, name);
        if (classFile == null) {
            throw new ClassNotFoundException(name);
        }
        try (InputStream is = new FileInputStream(classFile)) {
            byte[] b = new byte[is.available()];
            is.read(b);
            return defineClass(name, b, 0, b.length);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }
    }

    private File findFile(File root, String fileName, String className) {
        File[] files = root.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    File f = findFile(file, fileName, className);
                    if (f != null) {
                        return f;
                    }
                } else if (file.getName().equals(fileName)) {
                    String packageName = className.substring(0, className.lastIndexOf('.'));
                    String path = file.getAbsolutePath();
                    int index = path.indexOf(packageName.replace(".", File.separator));
                    if (index != -1) {
                        return file;
                    }
                }
            }
        }
        return null;
    }






    }

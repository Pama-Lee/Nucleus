package cn.devspace.nucleus.Units;

import cn.devspace.nucleus.Manager.ClassLoader.PluginClassLoader;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Server.Server;
import com.google.gson.Gson;
import org.apache.commons.lang.ClassUtils;
import org.apache.tomcat.Jar;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static java.lang.annotation.RetentionPolicy.CLASS;

public class Unit {
    public static Map<String ,Object> json2Map(String json){
        Map<String, Object> map = new HashMap<String, Object>();
        map = (Map<String, Object>) new Gson().fromJson(json,Map.class);
        return map;
    }

    public static <T> boolean isStartupFromJar(Class<T> clazz) {
        File file = new File(clazz.getProtectionDomain().getCodeSource().getLocation().getPath());
        return file.isFile();
    }

    public static boolean haveApp(String AppName){
        return Server.PluginList.containsKey(AppName) || Server.AppList.containsKey(AppName);
    }

    public static boolean checkParams(Map<String,String> maps, String[] params){
        for (String param : params) {
            if(!maps.containsKey(param)){
                return false;
            }
        }
        return true;
    }

    public static Set<String> getClassesFromJar(JarFile jar){
        Set<String> result = new HashSet<>();
        Enumeration<JarEntry> entrys = jar.entries();
        while (entrys.hasMoreElements()){
            JarEntry jarEntry = entrys.nextElement();
            if (jarEntry.getName().contains(".class")){
                String name = jarEntry.getName();
                name =  name.replace(".class","");
                name = name.replace("/",".");
                result.add(name);
            }
        }
        return result;
    }


    // 获取路径及子目录下的所有class文件
    // 需要通过文件目录获取class的包名
    public static Set<String> getClassesFromDir(File dir) {
        Set<String> result = new HashSet<>();
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() || pathname.getName().endsWith(".class");
            }
        });
        for (File file : files) {
            if (file.isDirectory()) {
                result.addAll(getClassesFromDir(file));
            } else {
                String name = file.getName();
                name = name.substring(0, name.length() - 6);
                result.add(getPackageName(file.getPath()));
            }
        }
        return result;
    }


    // 将路径转换为包名
    public static String getPackageName(String path) {
        String filePath = path;
        String root = "classes" + System.getProperty("file.separator");
        String classPath = filePath.substring(filePath.indexOf(root) + root.length());
        classPath = classPath.split("\\.")[0];
        String res= classPath.replace(System.getProperty("file.separator"), ".");

        return res;
    }

    /**
     * 获取ip
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            if (ipAddress != null && ipAddress.length() > 15) {
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress="";
        }
        return ipAddress;
    }

    public static String readConfigFile(JarFile jarFile ,JarEntry jarEntry) {
        String content = "";
        try {
            // 指定文件编码

            Charset charset = StandardCharsets.UTF_8;
            InputStream inputStream = jarFile.getInputStream(jarEntry);
            InputStreamReader reader = null;
            try{
                reader = new InputStreamReader(inputStream, charset);
            }catch (Exception e){
                // 如果文件编码不是UTF-8，就使用GBK编码
                inputStream = jarFile.getInputStream(jarEntry);
                reader = new InputStreamReader(inputStream, "GBK");
            }

            StringBuilder stringBuilder = new StringBuilder();
            int ch;
            while ((ch = reader.read()) != -1) {
                stringBuilder.append((char) ch);
            }

            content = stringBuilder.toString();

            inputStream.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }




    public static String getFileEncoding(String filePath) {
        String encoding = "";
        try {
            // 使用默认的文件编码读取文件
            FileInputStream inputStream = new FileInputStream(filePath);
            InputStreamReader reader = new InputStreamReader(inputStream);

            // 获取编码名称
            encoding = reader.getEncoding();

            inputStream.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encoding;
    }









}

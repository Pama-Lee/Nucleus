package cn.devspace.nucleus.Plugin.Config;

import cn.devspace.nucleus.Message.Log;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ConfigBase implements Config{

    private Map<String, Object> configMap = new HashMap<>();
    private String path;
    private String fileName;

    @Override
    public ConfigBase load(String path, String fileName) {
        // 读取配置文件转换为Map
        File file = new File(path + fileName);
        if (file.exists()) {
           Map<String, Object> map = new HashMap<>();
              // 读取文件
            try{
                InputStream inputStream = new FileInputStream(file);
                Map<String, Object> configMap = new Yaml().loadAs(inputStream, Map.class);
                if (configMap != null) {
                    this.configMap = configMap;
                }
                inputStream.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 创建文件
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.path = path;
        this.fileName = fileName;
        return this;
    }

    @Override
    public ConfigBase save() {
        // 将Map转换为Yaml并保存
        File file = new File(path + File.separator + fileName);
        if (file.exists()) {
            try {
                DumperOptions options = new DumperOptions();
                options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                options.setPrettyFlow(true);

                Yaml yaml = new Yaml(options);
                // 转换为Yaml文件
                String yamlString = yaml.dump(configMap);
                // 写入文件
                java.io.FileWriter fileWriter = new java.io.FileWriter(file);
                fileWriter.write(yamlString);
                fileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 创建文件
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public Object get(String key) {
        return configMap.get(key);
    }

    public ConfigBase set(String key, Object value) {
        if (configMap.containsKey(key)) {
            configMap.replace(key, value);
        } else {
            configMap.put(key, value);
        }
        return this;
    }

    public ConfigBase remove(String key) {
        configMap.remove(key);
        return this;
    }


}

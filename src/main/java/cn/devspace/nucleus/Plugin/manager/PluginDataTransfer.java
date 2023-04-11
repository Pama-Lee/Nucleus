package cn.devspace.nucleus.Plugin.manager;

import java.util.Map;

/**
 * 这个类用于提供插件之间的通信的数据传输
 */
public class PluginDataTransfer {
    public String pluginName;
    public String className;
    public String methodName;
    public Map<String, Object> args;
    private Map<String, Object> result;
    public Error error;

    public boolean isSuccessful;
    public String message;

    /**
     * 无参构造函数
     */
    public PluginDataTransfer(){
        this.isSuccessful = true;
        this.message = "成功";
    }

    /**
     * 具有报错信息的构造函数
     * @param message 报错信息
     */
    public PluginDataTransfer(String message){
        this.isSuccessful = false;
        this.message = message;
    }

    public void setResult(Object result) {
        // 如果result不是Map<String, Object>类型
        if (!(result instanceof Map)) {
            // 将其转换为Map类型
           this.result = PluginManager.createTable(result);
        }else {
            this.result = (Map<String, Object>) result;
        }
    }

    public Map<String, Object> getResult() {
        return result;
    }
}

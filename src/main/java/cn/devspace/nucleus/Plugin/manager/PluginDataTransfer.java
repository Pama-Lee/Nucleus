package cn.devspace.nucleus.Plugin.manager;

/**
 * 这个类用于提供插件之间的通信的数据传输
 */
public class PluginDataTransfer {
    public String pluginName;
    public String className;
    public String methodName;
    public Object[] args;
    public Object result;
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
}

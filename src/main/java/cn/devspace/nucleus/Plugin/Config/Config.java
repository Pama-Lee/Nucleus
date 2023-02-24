package cn.devspace.nucleus.Plugin.Config;

public interface Config {
    public Config load(String path, String fileName);
    public Config save();
    public Object get(String key);
    public Config set(String key, Object value);
    public Config remove(String key);


}

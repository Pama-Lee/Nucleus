package cn.devspace.nucleus.Plugin;

public interface Loader {

    //获取被加载项的详细信息
    public Description getDescription();

    public String getName();
}

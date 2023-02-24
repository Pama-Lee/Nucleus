package cn.devspace.nucleus.Entity;

import lombok.Data;

@Data
public class Router {

    public String URL;

    public String Method;

    public Class<?> Clazz;

    public String AppName;


}

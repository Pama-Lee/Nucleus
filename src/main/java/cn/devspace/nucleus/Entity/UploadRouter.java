package cn.devspace.nucleus.Entity;

import lombok.Data;

@Data
public class UploadRouter {
    private String URL;
    private String Method;
    private Class<?> Clazz;
    private String AppName;
}

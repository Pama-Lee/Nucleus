package cn.devspace.nucleus.Entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RouterClazz {

    public String RouteName;

    public Class<?> Clazz;

    public List<Router> Routers = new ArrayList<>();

}

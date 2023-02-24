package cn.devspace.nucleus.Manager.DataBase;

import cn.devspace.nucleus.Manager.Annotation.DataMapper;
import cn.devspace.nucleus.Manager.BeanManager;
import cn.devspace.nucleus.Plugin.DataEntity;
import cn.devspace.nucleus.Plugin.PluginBase;
import cn.devspace.nucleus.Server.Server;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import javax.xml.crypto.Data;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import static cn.devspace.nucleus.Server.Server.classLoaderManager;

public class AutoGenerator {

    public AutoGenerator() {
        for (String plugin : Server.PluginList.keySet()) {
            Set<Class<DataEntity>> entityClassSet = new HashSet<>();
            Set<Class<?>> ResourceClazz = new HashSet<>();
            String cPlugin = plugin;
            PluginBase pluginBase = Server.PluginList.get(plugin);
            if (!pluginBase.isEnabled()) {
                for (String clazz : pluginBase.allClazz) {
                    try {
                        ClassLoader urlClassLoader =  classLoaderManager.getClassLoader(pluginBase.classLoaderHashCode);
                        Class<?> clazs =  urlClassLoader.loadClass(clazz);
                        if (clazs.isAnnotationPresent(Entity.class) && DataEntity.class.isAssignableFrom(clazs)) {
                            Class<DataEntity> dataEntityClass = (Class<DataEntity>)clazs;
                            entityClassSet.add(dataEntityClass);
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
                for (Class<DataEntity> entityClass : entityClassSet) {

                    // 只创建带有 Entity 注解的实体
                    Entity tableName = entityClass.getAnnotation(Entity.class);
                    if (tableName == null) {
                        continue;
                    }
                    try{
                        DataBase dataBase = new DataBase(entityClass, entityClass.getConstructor().newInstance());
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }


                }
            }
        }
    }
}

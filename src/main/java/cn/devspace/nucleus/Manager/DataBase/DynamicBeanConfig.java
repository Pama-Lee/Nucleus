package cn.devspace.nucleus.Manager.DataBase;


import cn.devspace.nucleus.Manager.Annotation.DataMapper;
import cn.devspace.nucleus.Manager.BeanManager;
import cn.devspace.nucleus.Message.Log;
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
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;


import static cn.devspace.nucleus.Server.Server.classLoaderManager;

/**
 * 动态创建 mapper、service, class and bean
 */
@Configuration
@AutoConfigureAfter(DataSourceConfig.class)
public class DynamicBeanConfig {
    private final static Logger log = LoggerFactory.getLogger(DynamicBeanConfig.class);

    @Bean
    public SmartInitializingSingleton registerDynamicServicesAndMappers(SqlSessionFactory sqlSessionFactory) {
        return () -> {
            for (String plugin : Server.PluginList.keySet()) {
                Set<Class<?>> entityClassSet = new HashSet<>();
                Set<Class<?>> ResourceClazz = new HashSet<>();
                String cPlugin = plugin;
                PluginBase pluginBase = Server.PluginList.get(plugin);
                ClassLoader classLoader = pluginBase.getClass().getClassLoader();
                if (!pluginBase.isEnabled()){
                    for (String clazz:pluginBase.allClazz){
                        try {
                            URLClassLoader urlClassLoader = (URLClassLoader) classLoaderManager.getClassLoader(pluginBase.classLoaderHashCode);
                            Class<BaseMapper> clazs = (Class<BaseMapper>) urlClassLoader.loadClass(clazz);
                            if (clazs.isAnnotationPresent(TableName.class)){
                                    entityClassSet.add(clazs);
                            }
                            if (clazs.isAnnotationPresent(DataMapper.class)){
                                ResourceClazz.add(clazs);
                            }
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    for (Class<?> entityClass :entityClassSet ){

                        // 只创建带有 TableName 注解的实体
                        TableName tableName = entityClass.getAnnotation(TableName.class);
                        if (tableName == null) {
                            continue;
                        }

                        String mapperClassName = cPlugin + "." + entityClass.getSimpleName() + "Mapper";
                        String serviceClassName = cPlugin + "." + entityClass.getSimpleName() + "Service";

                        /* 创建 mapper */
                        Class<?> mapperClass = new ByteBuddy()
                                .makeInterface()
                                .implement(TypeDescription.Generic.Builder.parameterizedType(BaseMapper.class, entityClass).build())
                                .name(mapperClassName)
                                .annotateType(AnnotationDescription.Builder.ofType(Mapper.class).build())
                                .make()
                                .load(classLoader, new ClassLoadingStrategy.ForBootstrapInjection(null, null))
                                .getLoaded();

                        MapperFactoryBean<?> factoryBean = new MapperFactoryBean<>(mapperClass);
                        factoryBean.setSqlSessionFactory(sqlSessionFactory);

                        sqlSessionFactory.getConfiguration().addMapper(mapperClass);
                        // 注册 mapper
                        Object mapperInstance = null;
                        try {
                            SpringUtil.registerBean(getBeanName(mapperClassName), mapperInstance = factoryBean.getObject());
                        } catch (Exception e) {
                            log.warn("register BaseMapper error", e);
                        }

                        /* 创建 service */
                        Class<?> serviceClass = new ByteBuddy()
                                .subclass(TypeDescription.Generic.Builder.parameterizedType(ServiceImpl.class, mapperClass, entityClass).build())
                                .name(serviceClassName)
                                .annotateType(AnnotationDescription.Builder.ofType(Service.class).build())
                                .make()
                                .load(classLoader, new ClassLoadingStrategy.ForBootstrapInjection(null, null))
                                .getLoaded();

                        // 注册 service
                        try {
                            SpringUtil.registerBean(getBeanName(serviceClassName), serviceClass.getConstructor().newInstance());
                        } catch (Exception e) {
                            log.warn("register ServiceImpl error", e);
                        }

                    }
                    for (Class<?> resource : ResourceClazz){
                        BeanManager.registerBean(resource.getName(),resource);
                        BeanManager.getBean(resource.getName());
                    }
                }
            }
        };
    }

    // 根据类名获取 bean name
    private String getBeanName(String className) {
        int index = className.lastIndexOf(".");
        String simpleClassName = index != -1 ? className.substring(index + 1) : className;

        char firstChar = simpleClassName.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            firstChar -= 'A' - 'a';
        }
        return firstChar + simpleClassName.substring(1);
    }


}

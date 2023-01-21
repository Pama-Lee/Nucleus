package cn.devspace.nucleus.Manager;

import cn.devspace.nucleus.Manager.Annotation.InterfaceScan;
import cn.devspace.nucleus.Manager.DataBase.MyBatisConfig;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Plugin.PluginBase;
import cn.devspace.nucleus.Server.Server;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisMapperRegistry;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.Configuration;

import java.net.URLClassLoader;

import static cn.devspace.nucleus.Server.Server.classLoaderManager;


@Configuration
@InterfaceScan
public class BeanManager implements ApplicationContextAware {

    public static DefaultListableBeanFactory defaultListableBeanFactory;
    public static ApplicationContext applicationContexts;



    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Log.sendLog("Hello!!!");

        applicationContexts = applicationContext;
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
       // Log.sendLog(defaultListableBeanFactory.toString());

    }

    public static void registerBean(String beanName,Class<?> clazz){
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDefinitionBuilder.getBeanDefinition(), beanName);
        registerBeanDefinition(definitionHolder,defaultListableBeanFactory);
        //Log.sendLog(defaultListableBeanFactory.toString());
    }
    protected static void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
    }
    public static void getAllBeanString(){
        Log.sendLog(defaultListableBeanFactory.toString());
    }

    public static Object getBean(String beanName){
        return applicationContexts.getBean(beanName);
    }
}

package cn.devspace.nucleus.Manager;

import cn.devspace.nucleus.Message.Log;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;


@Configuration
public class BeanManager implements ApplicationContextAware {

    public static DefaultListableBeanFactory defaultListableBeanFactory;
    public static ApplicationContext applicationContexts;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
       // Log.sendLog("Hello!!!");
        applicationContexts = applicationContext;
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
       // Log.sendLog(defaultListableBeanFactory.toString());
    }

    public static void registerBean(String beanName,Class<?> clazz){
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        defaultListableBeanFactory.registerBeanDefinition(beanName,beanDefinitionBuilder.getRawBeanDefinition());
        //Log.sendLog(defaultListableBeanFactory.toString());
    }

    public static void getAllBeanString(){
        Log.sendLog(defaultListableBeanFactory.toString());
    }

    public static Object getBean(String beanName){
        return applicationContexts.getBean(beanName);
    }
}

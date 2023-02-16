package cn.devspace.nucleus.Manager.DataBase;

import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Plugin.DataEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class DataBase{
    private static SessionFactory concreteSessionFactory = null;
    protected Session session = null;

    protected Map<String, Session> sessionMap = new HashMap<>();

    public DataBase(Class<?> clazz,DataEntity dataEntity){
        Session session =  initDatabase(clazz,dataEntity,null);
        this.session = session;
    }

    public DataBase(Class<?> clazz,DataEntity dataEntity,Properties properties){
        Session session1 = initDatabase(clazz,dataEntity,properties);
        this.session = session1;
    }

    /**
     * 获取到当前DataBase默认的Session
     * @return
     */
    public Session getSession(){
        return this.session;
    }

    /**
     * 获取到由当前DataBase管理的特定名字的Session
     * @param name
     * @return
     */
    public Session getSession(String name){
        return session =  sessionMap.get(name);
    }

    /**
     * 通过传入名称创建一个新Session
     * @param name
     * @param clazz
     * @param dataEntity
     * @return
     */
    public Session newSession(String name,Class<?> clazz,DataEntity dataEntity){
        Session session = getSession(name);
        if (session == null){
            Session session_new = initDatabase(clazz,dataEntity,null);
            sessionMap.put(name,session_new);
            return session_new;
        }else {
            return session;
        }
    }

    public static Session initDatabase(Class<?> clazz,DataEntity dataEntity,Properties properties) {
        try {
            Properties prop;
            if (properties == null){
                prop = new Properties();

                prop.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/login");
                prop.setProperty("hibernate.connection.username", "root");
                prop.setProperty("hibernate.connection.password", "root");
                prop.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
                prop.setProperty("hibernate.show_sql", "true");
                prop.setProperty("hibernate.format_sql", "true");
                prop.setProperty("dialect", "org.hibernate.dialect.Mysql8Dialect");
                prop.setProperty("hibernate.hbm2ddl.auto", "update");
                prop.setProperty("org.hibernate", "NONE");
            }else {
                prop = properties;
            }
           // Log.sendLog(Server.PluginList.toString());
            BootstrapServiceRegistry bootstrapServiceRegistry = new BootstrapServiceRegistryBuilder().applyClassLoader(clazz.getClassLoader()).build();
            concreteSessionFactory = new Configuration(bootstrapServiceRegistry)
                    .addProperties(prop)
                    .addAnnotatedClass(dataEntity.getClass())
                    .buildSessionFactory()
            ;
            Session session = concreteSessionFactory.openSession();
            session.beginTransaction();
            return session;
        } catch (Exception e) {
            Log.sendWarn(e.toString());
        }
        return null;
    }

}
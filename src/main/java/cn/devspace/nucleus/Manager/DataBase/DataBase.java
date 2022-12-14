package cn.devspace.nucleus.Manager.DataBase;

import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Plugin.DataEntity;
import cn.devspace.nucleus.Server.Server;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class DataBase {
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

    public Session getSession(){
        return this.session;
    }

    public Session getSession(String name){
        return sessionMap.get(name);
    }

    public Session newSession(String name){
        Session session = concreteSessionFactory.openSession();
        sessionMap.put(name,session);
        return session;
    }

    public static Session initDatabase(Class<?> clazz,DataEntity dataEntity,Properties properties) {
        try {
            Properties prop;
            if (properties == null){
                prop = new Properties();
                Logger logger = Logger.getLogger(DataBase.class);
                logger.isDebugEnabled();
                logger.trace("ERROR");
                prop.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/login");
                prop.setProperty("hibernate.connection.username", "root");
                prop.setProperty("hibernate.connection.password", "ljk1249072779");
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
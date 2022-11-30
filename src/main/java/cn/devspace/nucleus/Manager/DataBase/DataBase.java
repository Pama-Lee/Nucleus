package cn.devspace.nucleus.Manager.DataBase;

import cn.devspace.nucleus.Manager.Annotation.version.Nucleus;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Plugin.DataEntity;
import cn.devspace.nucleus.Server.Server;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class DataBase {
    private static SessionFactory concreteSessionFactory = null;
    protected Session session = null;

    protected Map<String, Session> sessionMap = new HashMap<>();

    public DataBase(DataEntity dataEntity){
        Session session = initDatabase(dataEntity,null);
        sessionMap.put(dataEntity.ClassName(), session);
    }

    @Nucleus("0.0.2-alpha")
    public DataBase(DataEntity dataEntity,Properties properties){
        Session session = initDatabase(dataEntity,properties);
        sessionMap.put(dataEntity.ClassName(), session);
    }

    public DataBase(DataEntity dataEntity, String SessionName){
        Session session = initDatabase(dataEntity,null);
        sessionMap.put(SessionName,session);
    }

    /**
     * 初始化Hibernate数据库
     * @param dataEntity Entity实体
     * @param properties 自定义配置的配置文件
     * @param SessionName 自定义会话名
     */
    public DataBase(DataEntity dataEntity,Properties properties,String SessionName){
        Session session = initDatabase(dataEntity,properties);
        sessionMap.put(SessionName,session);
    }


    /**
     * 获取该database的会话
     * @param SessionName 传入SessionName或Entity类的类名
     * @return 返回一个会话
     */
    public Session getSession(String SessionName){
        return sessionMap.get(SessionName);
    }

    @Nucleus("0.0.2-alpha")
    public void reloadDatabase(DataEntity dataEntity, Properties properties){

    }

    /**
     * 初始化Hibernate数据库
     * @param dataEntity 数据实体
     * @return 返回数据库会话
     */
        private static Session initDatabase(DataEntity dataEntity,Properties properties) {
            try {
                Properties prop = null;
                if (properties == null){
                    prop = new Properties();
                    prop.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/login");
                    prop.setProperty("hibernate.connection.username", "root");
                    prop.setProperty("hibernate.connection.password", "");
                    prop.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
                    prop.setProperty("hibernate.show_sql", "false");
                    prop.setProperty("hibernate.format_sql", "false");
                    prop.setProperty("dialect", "org.hibernate.dialect.Mysql8Dialect");
                    prop.setProperty("hbm2ddl.auto", "update");
                    prop.setProperty("org.hibernate", "NONE");
                }else {
                    prop = properties;
                }

                BootstrapServiceRegistry bootstrapServiceRegistry = new BootstrapServiceRegistryBuilder().applyClassLoader(Server.PluginList.get("SimplePlugin").getClass().getClassLoader()).build();
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

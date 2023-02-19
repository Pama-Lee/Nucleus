package cn.devspace.nucleus.App.VisitLobby;

import cn.devspace.nucleus.App.VisitLobby.Entity.Visit;
import cn.devspace.nucleus.Manager.DataBase.DataBase;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Plugin.AppBase;
import org.hibernate.Session;
import cn.devspace.nucleus.App.VisitLobby.Queue.visitQueue;

public class Main extends AppBase {

    public static Main main = null;

    private Session visitSession = null;

    private visitQueue visitQueue = null;

    public static Main getInstance() {
        if (main == null) Log.sendWarn("VisitLobby is not loaded. Please check your configuration.");
        return main;
    }

    @Override
    public void onLoad() {
        sendLog("VisitLobby is loading....");
        // 初始化数据库
        DataBase dataBase = new DataBase(this.getClass(), new Visit());
        this.visitSession = dataBase.getSession();
        // 初始化队列
        this.visitQueue = new visitQueue();

        main = this;
    }

    @Override
        public void onEnable() {
          sendLog("VisitLobby is enabled.");
        }

    public Session getVisitSession() {
        if (visitSession == null){
            Log.sendWarn("VisitLobby is not loaded. Please check your configuration.");
            return null;
        }
        if (!visitSession.isOpen()) visitSession = visitSession.getSessionFactory().openSession();
        if (!visitSession.getTransaction().isActive()) visitSession.beginTransaction();
        return visitSession;
    }

    public static void newVisit(Visit visit) {
        if (main == null) {
            Log.sendWarn("VisitLobby is not loaded. Please check your configuration.");
            return;
        }
        main.visitQueue.newVisit(visit);
    }

    /**
     * 获取访问总数
     * @return
     */
    public static int getVisitCount(){
        if (main == null) {
            Log.sendWarn("VisitLobby is not loaded. Please check your configuration.");
            return 0;
        }
        return main.getVisitSession().createQuery("from Visit").list().size();
    }

    /**
     * 获取指定app的访问总数
     * @param app
     * @return
     */
    public static int getVisitCount(String app){
        if (main == null) {
            Log.sendWarn("VisitLobby is not loaded. Please check your configuration.");
            return 0;
        }
        return main.getVisitSession().createQuery("from Visit where app = :app").setParameter("app", app).list().size();
    }
}

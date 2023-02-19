package cn.devspace.nucleus.App.VisitLobby.Thread;

import cn.devspace.nucleus.App.VisitLobby.Entity.Visit;
import cn.devspace.nucleus.App.VisitLobby.Main;
import cn.devspace.nucleus.Message.Log;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.hibernate.Session;

import java.util.concurrent.BlockingQueue;

public class visitorThread implements Runnable {
        private final BlockingQueue<Visit> visitQueue;
        public visitorThread(BlockingQueue<Visit> visitQueue) {
                this.visitQueue = visitQueue;
        }
        @Override
        public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                        try {
                                Visit visit = visitQueue.take(); // 从消息队列取出一条访问记录
                                // 插入数据库
                                Session session = Main.getInstance().getVisitSession();
                                if (session == null) {
                                        Log.sendWarn("VisitLobby is not loaded. Please check your configuration.");
                                        return;
                                }
                                session.save(visit);
                                session.getTransaction().commit();
                        } catch (InterruptedException e) {
                                Log.sendWarn("VisitLobby is not loaded. Please check your configuration.");
                                Log.sendWarn(e.getMessage());
                        }
                }
        }

}

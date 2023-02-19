package cn.devspace.nucleus.App.VisitLobby.Queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import cn.devspace.nucleus.App.VisitLobby.Entity.Visit;
import cn.devspace.nucleus.App.VisitLobby.Thread.visitorThread;

public class visitQueue {

    public final static int QUEUE_SIZE = 100;
    private final BlockingQueue<Visit> visitQueue;
    private final Thread visitThread;

    public visitQueue() {
        visitQueue = new ArrayBlockingQueue<>(QUEUE_SIZE); // 消息队列容量为100
        visitThread = new Thread(new visitorThread(visitQueue)); // 创建邮件处理线程
        visitThread.start(); // 启动邮件处理线程
    }

    public void newVisit(Visit visit) {
        try {
            visitQueue.put(visit); // 将记录放入消息队列
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}

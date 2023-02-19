package cn.devspace.nucleus.App.MailLobby.Threads;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class EmailQueue {
    private final BlockingQueue<Email> emailQueue;
    private final Thread emailThread;

    public EmailQueue() {
        emailQueue = new ArrayBlockingQueue<>(100); // 消息队列容量为100
        emailThread = new Thread(new SenderThread(emailQueue)); // 创建邮件处理线程
        emailThread.start(); // 启动邮件处理线程
    }

    public void addEmail(Email email) throws InterruptedException {
        emailQueue.put(email); // 将邮件加入消息队列
    }

    public void close() {
        emailThread.interrupt(); // 中断邮件处理线程
    }

}


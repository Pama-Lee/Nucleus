package cn.devspace.nucleus.App.MailLobby.Threads;

import cn.devspace.nucleus.Message.Log;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;

import java.util.concurrent.BlockingQueue;

public class SenderThread extends Thread{
    private final BlockingQueue<Email> emailQueue;
    public SenderThread(BlockingQueue<Email> emailQueue) {
        this.emailQueue = emailQueue;
    }
    public void run(){
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Email email = emailQueue.take(); // 从消息队列取出一封邮件
                email.send(); // 发送邮件
            } catch (InterruptedException | EmailException e) {
                Log.sendWarn("Email sending failed: " + e.getMessage());
            }
        }
    }
}

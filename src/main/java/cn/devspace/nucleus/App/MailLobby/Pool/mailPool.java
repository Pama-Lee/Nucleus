package cn.devspace.nucleus.App.MailLobby.Pool;

import cn.devspace.nucleus.App.MailLobby.Threads.EmailQueue;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

// 邮件池
public class mailPool {
    private final BlockingQueue<Email> emailPool;
    private final EmailQueue emailQueue;

    public mailPool(int size) {
        emailPool = new ArrayBlockingQueue<>(size); // 对象池容量为size
        emailQueue = new EmailQueue(); // 创建邮件发送队列
        for (int i = 0; i < size; i++) {
            emailPool.offer(new SimpleEmail()); // 创建对象并加入对象池
        }
    }

    public Email borrowEmail(long timeout) throws InterruptedException {
        Email email = emailPool.poll(timeout, TimeUnit.MILLISECONDS); // 从对象池中获取一个邮件对象
        if (email != null) {
            return email;
        } else {
            throw new RuntimeException("Email pool timeout"); // 抛出超时异常
        }
    }

    public void returnEmail(Email email) throws InterruptedException {
        emailPool.offer(email, 500, TimeUnit.MILLISECONDS); // 将邮件对象放回对象池
    }

    public void sendEmail(Email email) throws InterruptedException {
        emailQueue.addEmail(email); // 将邮件加入消息队列
    }

    public void close() {
        emailQueue.close(); // 关闭邮件发送队列
    }


}

package cn.devspace.nucleus.App.MailLobby.Threads;

import org.apache.commons.mail.Email;
import org.springframework.mail.MailSender;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class EmailSender extends Thread{

    public EmailSender() {

    }

    @Override
    public void run() {
        // 获取Queue
        EmailQueue emailQueue = EmailQueue.getInstance();
        while(!this.isInterrupted()){
            // 获取邮件列表
            List<Email> emailList = emailQueue.getEmailList();
            if (emailList.size() == 0) {
                // 等待10秒
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            for (int i = 0; i < emailList.size(); i++) {
                // 发送邮件
                try {
                    Email email = emailList.get(i);
                    email.send();
                    EmailQueue.getInstance().removeEmail(email);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 清空邮件列表
            emailList = null;
            // 等待
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


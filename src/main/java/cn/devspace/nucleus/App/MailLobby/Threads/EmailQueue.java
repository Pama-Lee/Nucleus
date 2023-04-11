package cn.devspace.nucleus.App.MailLobby.Threads;
import org.apache.commons.mail.Email;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class EmailQueue {
    // 单例模式
    private static EmailQueue emailQueue = new EmailQueue();
    private final CopyOnWriteArrayList<Email> emailList = new CopyOnWriteArrayList<>();

    private EmailSender emailSender = new EmailSender();

    public static EmailQueue getInstance() {
        if (emailQueue == null) {
            emailQueue = new EmailQueue();
        }
        return emailQueue;
    }

    public void addEmail(Email email) {
        // 检查emailSender是否死亡
        if (!emailSender.isAlive()) {
            emailSender = new EmailSender();
            emailSender.start();
        }
        emailList.add(email);
    }

    public void removeEmail(Email email) {
        emailList.remove(email);
    }

    public List<Email> getEmailList() {
        return emailList;
    }



}



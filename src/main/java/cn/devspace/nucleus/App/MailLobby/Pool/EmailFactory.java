package cn.devspace.nucleus.App.MailLobby.Pool;

import cn.devspace.nucleus.App.MailLobby.MailConfiguration;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class EmailFactory extends BasePooledObjectFactory<Email> {

    MailConfiguration mailConfiguration = new MailConfiguration();
        @Override
        public Email create() throws Exception {
            Email email = new SimpleEmail();

            // 设置邮件相关属性
            email.setHostName(mailConfiguration.getHostName());
            email.setSmtpPort(mailConfiguration.getPort());
            email.setAuthenticator(new DefaultAuthenticator(mailConfiguration.getUsername(), mailConfiguration.getPassword()));
            return email;
        }

        @Override
        public PooledObject<Email> wrap(Email email) {
            return new DefaultPooledObject<>(email);
        }
    }

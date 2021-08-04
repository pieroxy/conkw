package net.pieroxy.conkw.webapp.grabbers.email;

import javax.mail.Message;
import javax.mail.MessagingException;

public class MailData {
    public MailData(ImapConfig conf, Message m) throws MessagingException {
        this.account = conf.getName();
        date = m.getSentDate().getTime();
        from = MailTools.getFrom(m);
        subject = m.getSubject();
    }
    private String account;
    private long date;
    private String from;
    private String subject;

    public String getAccount() {
        return account;
    }

    public long getDate() {
        return date;
    }

    public String getFrom() {
        return from;
    }

    public String getSubject() {
        return subject;
    }
}

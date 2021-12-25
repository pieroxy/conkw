package net.pieroxy.conkw.webapp.grabbers.email;

import net.pieroxy.conkw.collectors.SimpleCollector;
import net.pieroxy.conkw.utils.StringUtil;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.grabbersBase.TimeThrottledGrabber;
import net.pieroxy.conkw.utils.hashing.Md5Sum;

import java.util.Properties;
import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.util.*;
import java.util.logging.Level;

public class LatestUnreadMailsGrabber extends TimeThrottledGrabber<LatestUnreadMailsGrabber.LatestUnreadMailsGrabberConfig> {
    static final String NAME = "mails";

    private static Message[] getMessages(ImapConfig conf, boolean debug) throws MessagingException {
        if (isValidPassword(conf.getPassword())) {
            Session session = Session.getDefaultInstance(new Properties());
            session.setDebug(debug);
            Store store = session.getStore("imaps");
            store.connect(conf.getServer(), (int) (double) conf.getPort(), conf.getLogin(), conf.getPassword());
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // Fetch unseen messages from inbox folder
            Message[] messages = inbox.search(
                    new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            return messages;
        } else {
            return new Message[0];
        }
    }

    private static boolean isValidPassword(String password) {
        return !StringUtil.isNullOrEmptyTrimmed(password);
    }

    @Override
    public LatestUnreadMailsGrabberConfig getDefaultConfig() {
        LatestUnreadMailsGrabberConfig c = new LatestUnreadMailsGrabberConfig();
        c.setTtl(CDuration.FIVE_MINUTES);
        c.setMaxMessages(10.);
        return c;
    }

    @Override
    public String getDefaultName() {
        return NAME;
    }

    @Override
    protected void load(SimpleCollector res) {
        long now = System.currentTimeMillis();
        log(Level.FINE, "Starting to grab mails");
        List<MailData> allMails = new ArrayList<>();
        for (ImapConfig c : getConfig().getAccounts()) {
            try {
                Message[]ms = getMessages(c, this.canLogFiner());
                if (shutdownRequested()) return;

                for (Message m : ms) {
                    allMails.add(new MailData(c, m));
                    if (shutdownRequested()) return;
                }
            } catch (MessagingException e) {
                res.addError("Unable to fetch mail for " + c.getLogin() + ": " + e.getMessage());
                log(Level.SEVERE, "", e);
            }
        }
        Collections.sort(allMails, Comparator.comparingLong(m -> -m.getDate()));

        int i=0;
        for (MailData m : allMails) {
            res.collect("mail_" + i + "_subject", m.getSubject());
            res.collect("mail_" + i + "_date", m.getDate());
            res.collect("mail_" + i + "_account", m.getAccount());
            res.collect("mail_" + i + "_from", m.getFrom());
            i++;
            if (i>=getConfig().getMaxMessages()) break;
        }
        res.collect("mails_len", i);
        res.collect("mails_unread_total", allMails.size());
        log(Level.FINE, "Done grabbing mails in " + (System.currentTimeMillis() - now) + "ms.");
    }

    public static class LatestUnreadMailsGrabberConfig extends TimeThrottledGrabber.TimeThrottledGrabberConfig {
        private Double maxMessages;
        private List<ImapConfig> accounts;

        @Override
        public void addToHash(Md5Sum sum) {
            sum.add(String.valueOf(maxMessages));
            getAccounts().stream().forEach(ic -> ic.addToHash(sum));
        }

        public Double getMaxMessages() {
            return maxMessages;
        }

        public void setMaxMessages(Double maxMessages) {
            this.maxMessages = maxMessages;
        }

        public List<ImapConfig> getAccounts() {
            return accounts;
        }

        public void setAccounts(List<ImapConfig> accounts) {
            this.accounts = accounts;
        }
    }
}
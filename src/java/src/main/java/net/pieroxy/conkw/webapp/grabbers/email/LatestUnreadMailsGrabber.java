package net.pieroxy.conkw.webapp.grabbers.email;

import net.pieroxy.conkw.utils.HashTools;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.duration.CDurationParser;
import net.pieroxy.conkw.webapp.grabbers.TimeThrottledGrabber;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.util.*;
import java.util.logging.Level;

public class LatestUnreadMailsGrabber extends TimeThrottledGrabber {
    static final String NAME = "mails";

    private CDuration ttl = CDurationParser.parse("5m");
    private int maxMessages = 10;
    private String cackeKey = "nothing";
    private List<ImapConfig> configurations = new ArrayList<>();

    public static void main( String[] args ) throws Exception {
        System.out.println("Usage: LatestMailsGrabber imapserver user password");

        ImapConfig conf = new ImapConfig();

        conf.setServer(args[0]);
        conf.setPort(Integer.parseInt(args[1]));
        conf.setLogin(args[2]);
        conf.setPassword(args[3]);

        System.out.println("IMAP Server " + conf.getServer());
        System.out.println("Server port " + conf.getPort());
        System.out.println("User name   " + conf.getLogin());
        System.out.println("Password    " + conf.getPassword());

        Message[] messages = getMessages(conf, true);

        // Sort messages from recent to oldest
        Arrays.sort( messages, ( m1, m2 ) -> {
            try {
                return m2.getSentDate().compareTo( m1.getSentDate() );
            } catch ( MessagingException e ) {
                throw new RuntimeException( e );
            }
        });

        for ( Message message : messages ) {
            long uid = -1;
            Folder f = message.getFolder();
            if (f instanceof UIDFolder) {
                UIDFolder uf = (UIDFolder) f;
                uid = uf.getUID(message);
            }
            System.out.println(
                    "sendDate: " + message.getSentDate()
                            + " uid: " + uid
                            + " from: " + MailTools.getFrom(message)
                  + " subject:" + message.getSubject() );
        }
    }

    private static Message[] getMessages(ImapConfig conf, boolean debug) throws MessagingException {
        Session session = Session.getDefaultInstance(new Properties());
        session.setDebug(debug);
        Store store = session.getStore("imaps");
        store.connect(conf.getServer(), conf.getPort(), conf.getLogin(), conf.getPassword());
        Folder inbox = store.getFolder( "INBOX" );
        inbox.open( Folder.READ_ONLY );

        // Fetch unseen messages from inbox folder
        Message[] messages = inbox.search(
                new FlagTerm(new Flags(Flags.Flag.SEEN), false));
        return messages;
    }


    @Override
    public String getDefaultName() {
        return NAME;
    }

    @Override
    protected void setConfig(Map<String, String> config) {
        ttl = getDurationProperty("ttl", config, ttl);
        maxMessages = getIntProperty("maxMessages", config, maxMessages);
        configurations = new ArrayList<>();
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log(Level.SEVERE, "", e);
            return;
        }
        md.update(String.valueOf(maxMessages).getBytes(StandardCharsets.UTF_8));
        for (int i=0 ; true ; i++) {
            String conf = config.get("imapConf" + i);
            if (conf == null) break;
            md.update(conf.getBytes(StandardCharsets.UTF_8));
            int p1 = conf.indexOf(':');
            int p2 = conf.indexOf(':', p1+1);
            int p3 = conf.indexOf(':', p2+1);
            int p4 = conf.indexOf(':', p3+1);
            ImapConfig c = new ImapConfig();
            c.setName(conf.substring(0, p1));
            c.setServer(conf.substring(p1 + 1, p2));
            c.setPort(Integer.parseInt(conf.substring(p2 + 1, p3)));
            c.setLogin(conf.substring(p3 + 1, p4));
            c.setPassword(conf.substring(p4 + 1));
            configurations.add(c);
        }
        cackeKey = HashTools.byteArrayToHexString(md.digest());
    }

    @Override
    protected CDuration getTtl() {
        return ttl;
    }

    @Override
    protected void load(ResponseData res) {
        long now = System.currentTimeMillis();
        log(Level.FINE, "Starting to grab mails");
        List<MailData> allMails = new ArrayList<>();
        for (ImapConfig c : configurations) {
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
            res.addMetric("mail_" + i + "_subject", m.getSubject());
            res.addMetric("mail_" + i + "_date", m.getDate());
            res.addMetric("mail_" + i + "_account", m.getAccount());
            res.addMetric("mail_" + i + "_from", m.getFrom());
            i++;
            if (i>=maxMessages) break;
        }
        res.addMetric("mails_len", i);
        res.addMetric("mails_unread_total", allMails.size());
        log(Level.FINE, "Done grabbing mails in " + (System.currentTimeMillis() - now) + "ms.");
    }

    @Override
    protected String getCacheKey() {
        return cackeKey;
    }

}
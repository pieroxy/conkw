package net.pieroxy.conkw.webapp.grabbers;

import net.pieroxy.conkw.utils.HashTools;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.duration.CDurationParser;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
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

        System.out.println("IMAP Server " + (conf.server = args[0]));
        System.out.println("Server port " + (conf.port=Integer.parseInt(args[1])));
        System.out.println("User name   " + (conf.login=args[2]));
        System.out.println("Password    " + (conf.password=args[3]));

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
            System.out.println(
                    "sendDate: " + message.getSentDate()
                  + "from: " + getFrom(message)
                  + " subject:" + message.getSubject() );
        }
    }

    private static Message[] getMessages(ImapConfig conf, boolean debug) throws MessagingException {
        Session session = Session.getDefaultInstance(new Properties());
        session.setDebug(debug);
        Store store = session.getStore("imaps");
        store.connect(conf.server, conf.port, conf.login, conf.password);
        Folder inbox = store.getFolder( "INBOX" );
        inbox.open( Folder.READ_ONLY );

        // Fetch unseen messages from inbox folder
        Message[] messages = inbox.search(
                new FlagTerm(new Flags(Flags.Flag.SEEN), false));
        return messages;
    }

    private static String getFrom(Message message) throws MessagingException {
        Address[] from = message.getFrom();
        StringBuilder sb = new StringBuilder();
        for (Address a : from) {
            if (sb.length()>1) sb.append(" ");
            sb.append(getNiceMailAddress(a));
        }
        return sb.toString();
    }

    private static String getNiceMailAddress(Address address) throws MessagingException {
        if (address instanceof InternetAddress) {
            InternetAddress ia = (InternetAddress) address;
            return ia.getPersonal() + " &lt;" + ia.getAddress() + "&gt;";
        } else {
            return address.toString();
        }
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
            c.name = conf.substring(0, p1);
            c.server = conf.substring(p1+1, p2);
            c.port = Integer.parseInt(conf.substring(p2+1, p3));
            c.login = conf.substring(p3+1, p4);
            c.password = conf.substring(p4+1);
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
                for (Message m : ms) {
                    allMails.add(new MailData(c, m));
                }
            } catch (MessagingException e) {
                res.addError("Unable to fetch mail for " + c.login + ": " + e.getMessage());
                log(Level.SEVERE, "", e);
            }
        }
        Collections.sort(allMails, Comparator.comparingLong(m -> -m.date));

        int i=0;
        for (MailData m : allMails) {
            res.addMetric("mail_" + i + "_subject", m.subject);
            res.addMetric("mail_" + i + "_date", m.date);
            res.addMetric("mail_" + i + "_account", m.account);
            res.addMetric("mail_" + i + "_from", m.from);
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

    private static class ImapConfig {
        private String name;
        private String server;
        private int port;
        private String login;
        private String password;
    }
    private static class MailData {
        public MailData(ImapConfig conf, Message m) throws MessagingException {
            this.account = conf.name;
            date = m.getSentDate().getTime();
            from = getFrom(m);
            subject = m.getSubject();
        }
        private String account;
        private long date;
        private String from;
        private String subject;
    }
}
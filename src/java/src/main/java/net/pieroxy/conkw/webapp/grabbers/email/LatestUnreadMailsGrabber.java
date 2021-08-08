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
import java.util.stream.Collectors;

public class LatestUnreadMailsGrabber extends TimeThrottledGrabber {
    static final String NAME = "mails";

    private CDuration ttl = CDurationParser.parse("5m");
    private int maxMessages = 10;
    private String cackeKey = "nothing";
    private List<ImapConfig> configurations = new ArrayList<>();

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
    protected void setConfig(Map<String, String> config, Map<String, Map<String, String>> configs) {
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

        for (String name : configs.keySet().stream().sorted().collect(Collectors.toList())) {
            ImapConfig ic = new ImapConfig(name, configs.get(name));
            configurations.add(ic);
            md.update(ic.getUniqueContentString().getBytes(StandardCharsets.UTF_8));
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
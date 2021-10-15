package net.pieroxy.conkw.webapp.grabbers.email;

import com.sun.mail.imap.IMAPFolder;
import net.pieroxy.conkw.collectors.SimpleCollector;
import net.pieroxy.conkw.utils.HashTools;
import net.pieroxy.conkw.utils.StringUtil;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.duration.CDurationParser;
import net.pieroxy.conkw.grabbersBase.TimeThrottledGrabber;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapPool;
import net.pieroxy.conkw.webapp.model.ResponseData;

import javax.mail.*;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Pattern;


public class SpecificEmailCheckGrabber extends TimeThrottledGrabber {
    static final String NAME = "specificmail";
    private static final String LAST_SEEN = "last_seen";
    private static final String NEXT_UID = "next_uid";
    private String cackeKey = "nothing";

    private String folder;
    private Pattern subjectRegexp;
    private Pattern senderRegexp;
    private Pattern bodyRegexp;
    private ImapConfig imapConf;
    private Long nextUID;
    private long lastSeen = 0;

    @Override
    public String getDefaultName() {
        return NAME;
    }

    @Override
    protected void cacheLoaded(Map<String,ResponseData> dataset, Map<String, String> privateData) {
        ResponseData data = dataset.get(DEFAULT_CONFIG_KEY);
        if (data == null) return;
        Double d = data.getNumMetric(LAST_SEEN);
        if (d!=null) lastSeen = (long)(double)d;
        if (privateData != null) {
            String nuid = privateData.get(NEXT_UID);
            if (nuid!=null) nextUID = Long.parseLong(nuid);
        }
    }

    @Override
    protected void applyConfig(Map<String, String> config, Map<String, Map<String, String>> configs) {
        if (config==null || configs==null) return;
        if (configs.get("imapConf")==null) return;
        if (StringUtil.isNullOrEmptyTrimmed(configs.get("imapConf").get("password"))) return;
        
        folder = getStringProperty("folder", config, "INBOX");
        subjectRegexp = getRegexpProperty("subjectRegexp", config, 0);
        bodyRegexp = getRegexpProperty("bodyRegexp", config, Pattern.DOTALL);
        senderRegexp = getRegexpProperty("senderRegexp", config, 0);
        imapConf = new ImapConfig("imap", configs.get("imapConf"));

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log(Level.SEVERE, "", e);
            return;
        }
        md.update((folder+subjectRegexp+bodyRegexp+senderRegexp+imapConf).getBytes(StandardCharsets.UTF_8));
        cackeKey = HashTools.byteArrayToHexString(md.digest());
    }

    @Override
    protected CDuration getDefaultTtl() {
        return CDurationParser.parse("5m");
    }

    @Override
    protected void load(SimpleCollector res) {
        try {
            Message[] messages = getMessages();
            if (messages!=null && messages.length>0) {
                String error = searchForMessage(messages);
                if (error!=null) res.addError(error);
                storeNextUid(messages);
            }
            res.collect(LAST_SEEN, lastSeen);
            setPrivateData();
        } catch (Exception e) {
            log(Level.SEVERE, "", e);
            res.addError(e.getMessage());
        }
    }

    private void setPrivateData() {
        if (nextUID == null) return;
        Map<String, String> pd = HashMapPool.getInstance().borrow(1);
        pd.put(NEXT_UID, nextUID +"");
        setPrivateData(pd);
    }

    private void storeNextUid(Message[] messages) throws MessagingException {
        nextUID = MailTools.getNextUid(messages[0]);
    }

    private String searchForMessage(Message[] messages) {
        for (Message m : messages) {
            try {
                if (lastSeen < m.getSentDate().getTime() && matches(m)) {
                    lastSeen = Math.max(lastSeen, m.getSentDate().getTime());
                }
            } catch (Exception e) {
                log(Level.SEVERE, "", e);
                return e.getMessage();
            }
        }
        return null;
    }

    private boolean matches(Message m) throws Exception {
        if (subjectRegexp!=null) {
            if (canLogFine()) log(Level.FINE, "Checking re " + subjectRegexp + " in subject for message " + m.getSubject());
            if (!subjectRegexp.matcher(m.getSubject()).matches()) return false;
        }
        if (senderRegexp!=null) {
            if (canLogFine()) {
                log(Level.FINE, "Checking sender for message " + m.getSubject());
                log(Level.FINE, "Address is '" + MailTools.getMailAddress(m.getFrom()[0]) + "'");
            }
            if (!senderRegexp.matcher(MailTools.getMailAddress(m.getFrom()[0])).matches()) return false;
        }
        if (bodyRegexp!=null) {
            if (canLogFine())log(Level.INFO, "Checking body for message " + m.getSubject());
            if (!bodyRegexp.matcher(MailTools.getPlainContent(m.getContent())).matches()) return false;
        }
        if (canLogFine()) log(Level.FINE, "Success for message " + m.getSubject());
        return true;
    }

    private Message[] getMessages() throws MessagingException {
        Session session = Session.getDefaultInstance(new Properties());
        session.setDebug(canLogFine());
        Store store = session.getStore("imaps");
        store.connect(imapConf.getServer(), imapConf.getPort(), imapConf.getLogin(), imapConf.getPassword());
        Folder inbox = store.getFolder(folder);
        inbox.open(Folder.READ_ONLY);
        if (canLogFine()) log(Level.FINE, "lastUID: " + nextUID);

        if (nextUID != null) {
            Message[] messages = ((IMAPFolder)inbox).getMessagesByUID(nextUID, UIDFolder.MAXUID);
            if (canLogFine()) log(Level.FINE, "From UID: " + messages.length);
            return messages;
        } else {
            Date d = new Date(System.currentTimeMillis() - CDurationParser.parse("5d").asMilliseconds());
            Message[] messages = inbox.search(new ReceivedDateTerm(ComparisonTerm.GT, d));
            if (canLogFine()) log(Level.FINE, "From date: " + messages.length);
            return messages;
        }
    }

    @Override
    protected String getCacheKey() {
        return cackeKey;
    }
}
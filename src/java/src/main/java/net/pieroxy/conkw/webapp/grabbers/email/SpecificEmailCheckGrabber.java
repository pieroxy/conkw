package net.pieroxy.conkw.webapp.grabbers.email;

import com.sun.mail.imap.IMAPFolder;
import net.pieroxy.conkw.accumulators.AccumulatorUtils;
import net.pieroxy.conkw.collectors.SimpleCollector;
import net.pieroxy.conkw.config.Credentials;
import net.pieroxy.conkw.grabbersBase.TimeThrottledGrabber;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.duration.CDurationParser;
import net.pieroxy.conkw.utils.hashing.Hashable;
import net.pieroxy.conkw.utils.hashing.Md5Sum;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapPool;
import net.pieroxy.conkw.webapp.model.ResponseData;

import javax.mail.*;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SpecificEmailCheckGrabber extends TimeThrottledGrabber<SpecificEmailCheckGrabber.SpecificEmailCheckGrabberConfig> {
    static final String NAME = "specificmail";
    private static final String LAST_SEEN = "last_seen";
    private static final String NEXT_UID = "next_uid";

    private Long nextUID;
    private long lastSeen = 0;
    private Map<String, Object> captured = new HashMap<>();

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
    public SpecificEmailCheckGrabberConfig getDefaultConfig() {
        SpecificEmailCheckGrabberConfig res = new SpecificEmailCheckGrabberConfig();
        res.setTtl(CDuration.FIVE_MINUTES);
        return res;
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
            captured.entrySet().forEach(entry -> {
                if (entry.getValue() instanceof String) res.collect(entry.getKey(), (String)entry.getValue());
                if (entry.getValue() instanceof Double) res.collect(entry.getKey(), (Double)entry.getValue());
            });
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
                if (lastSeen < m.getSentDate().getTime()) {
                    Map<String, Object> captured = matches(m);
                    if (captured!=null) {
                        lastSeen = Math.max(lastSeen, m.getSentDate().getTime());
                        this.captured = captured;
                    }
                }
            } catch (Exception e) {
                log(Level.SEVERE, "", e);
                return e.getMessage();
            }
        }
        return null;
    }

    private Map<String, Object> matches(Message m) throws Exception {
        if (getConfig().getSubjectRegexp()!=null) {
            if (canLogFine()) log(Level.FINE, "Checking re " + getConfig().getSubjectRegexp() + " in subject for message " + m.getSubject());
            if (!getConfig().getSubjectRegexp().matcher(m.getSubject()).matches()) return null;
        }
        if (getConfig().getSenderRegexp()!=null) {
            if (canLogFine()) {
                log(Level.FINE, "Checking sender for message " + m.getSubject());
                log(Level.FINE, "Address is '" + MailTools.getMailAddress(m.getFrom()[0]) + "'");
            }
            if (!getConfig().getSenderRegexp().matcher(MailTools.getMailAddress(m.getFrom()[0])).matches()) return null;
        }
        CharSequence body = null;
        if (getConfig().getBodyRegexp()!=null) {
            if (canLogFine()) log(Level.INFO, "Checking body for message " + m.getSubject());
            if (canLogFine()) log(Level.FINE, "BODY type is " + m.getContent().getClass().getName());
            body = MailTools.getPlainContent(m.getContent());
            if (canLogFine()) log(Level.FINE, "BODY decoded is " + body);
            if (!getConfig().getBodyRegexp().matcher(body).matches()) return null;
        }
        Map<String, Object> res = new HashMap<>();
        if (getConfig().getToCapture()!=null && !getConfig().getToCapture().isEmpty()) {
            if (body == null) body = MailTools.getPlainContent(m.getContent());
            if (canLogFine()) log(Level.FINE, "Capturing " + getConfig().getToCapture().size() + " groups.");
            res = extractContent(getConfig().getToCapture(), body);
        } else {
            if (canLogFine()) log(Level.FINE, "Nothing to capture");
        }
        if (canLogFine()) log(Level.FINE, "Success for message " + m.getSubject());
        return res;
    }

    private Map<String, Object> extractContent(List<SpecificEmailCheckGrabberPatternConfig> toExtract, CharSequence body) {
        Map<String, Object> res = new HashMap<>();
        for (SpecificEmailCheckGrabberPatternConfig p : toExtract) {
            String rootMetricName = p.getId();
            if (canLogFine()) log(Level.FINE, "Pattern: " + p.getPattern().pattern());
            Matcher m = p.getPattern().matcher(body);
            if (canLogFine()) log(Level.FINE, "Matches: " + m.matches() + " Groups: " + m.groupCount());
            res.put(AccumulatorUtils.addToMetricName(rootMetricName, "matched"), m.matches() ? 1d : 0d);
            res.put(AccumulatorUtils.addToMetricName(rootMetricName, "found"), getMatches(m));
            if (m.groupCount() == 1 && m.matches()) {
                String metricName = AccumulatorUtils.addToMetricName(rootMetricName, "captured");
                String values = m.group(1);
                if (canLogFine()) log(Level.FINE, "Captured: " + values);
                if (canLogFine()) log(Level.FINE, "With name: " + metricName);
                if (p.isNumber() != null && p.isNumber()) {
                    res.put(metricName, Double.parseDouble(values));
                } else {
                    res.put(metricName, values);
                }
            }
        }
        return res;
    }
    private double getMatches(Matcher m) {
        int res = 0;
        while(m.find()) res++;
        return res;
    }


    private Message[] getMessages() throws MessagingException {
        Credentials creds = getCredentials(getConfig().getImapConf());
        if (creds == null) {
            log(Level.WARNING, "No credentials for IMAP configuration " + getConfig().getImapConf());
        }
        Session session = Session.getDefaultInstance(new Properties());
        session.setDebug(canLogFine());
        Store store = session.getStore("imaps");
        store.connect(
            getConfig().getImapConf().getServer(),
            (int)(double)getConfig().getImapConf().getPort(),
            creds.getId(),
            creds.getSecret());
        Folder inbox = store.getFolder(getConfig().getFolder());
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

    public static class SpecificEmailCheckGrabberConfig extends TimeThrottledGrabber.SimpleTimeThrottledGrabberConfig {
        String folder;
        Pattern subjectRegexp;
        Pattern bodyRegexp;
        Pattern senderRegexp;
        ImapConfig imapConf;
        List<SpecificEmailCheckGrabberPatternConfig> toCapture;

        @Override
        public void addToHash(Md5Sum sum) {
            sum.add(folder).add(subjectRegexp.pattern()).add(bodyRegexp.pattern()).add(senderRegexp.pattern());
            getImapConf().addToHash(sum);
        }

        public String getFolder() {
            return folder;
        }

        public void setFolder(String folder) {
            this.folder = folder;
        }

        public Pattern getSubjectRegexp() {
            return subjectRegexp;
        }

        public void setSubjectRegexp(Pattern subjectRegexp) {
            this.subjectRegexp = subjectRegexp;
        }

        public Pattern getBodyRegexp() {
            return bodyRegexp;
        }

        public void setBodyRegexp(Pattern bodyRegexp) {
            this.bodyRegexp = bodyRegexp;
        }

        public Pattern getSenderRegexp() {
            return senderRegexp;
        }

        public void setSenderRegexp(Pattern senderRegexp) {
            this.senderRegexp = senderRegexp;
        }

        public ImapConfig getImapConf() {
            return imapConf;
        }

        public void setImapConf(ImapConfig imapConf) {
            this.imapConf = imapConf;
        }

        public List<SpecificEmailCheckGrabberPatternConfig> getToCapture() {
            return toCapture;
        }

        public void setToCapture(List<SpecificEmailCheckGrabberPatternConfig> toCapture) {
            this.toCapture = toCapture;
        }
    }
    public static class SpecificEmailCheckGrabberPatternConfig implements Hashable {
        private String id;
        private Pattern pattern;
        private Boolean number;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Pattern getPattern() {
            return pattern;
        }

        public void setPattern(Pattern pattern) {
            this.pattern = pattern;
        }

        public Boolean isNumber() {
            return number;
        }

        public void setNumber(Boolean number) {
            this.number = number;
        }

        @Override
        public void addToHash(Md5Sum sum) {
            sum.add(id).add(pattern.pattern()).add(number ? "true" : "false");
        }
    }

}
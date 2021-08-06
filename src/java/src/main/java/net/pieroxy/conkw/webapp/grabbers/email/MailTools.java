package net.pieroxy.conkw.webapp.grabbers.email;

import com.sun.mail.imap.IMAPFolder;
import org.apache.commons.mail.util.MimeMessageParser;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailTools {
    public static String getFrom(Message message) throws MessagingException {
        Address[] from = message.getFrom();
        StringBuilder sb = new StringBuilder();
        for (Address a : from) {
            if (sb.length()>1) sb.append(" ");
            sb.append(getNiceMailAddress(a));
        }
        return sb.toString();
    }

    public static String getNiceMailAddress(Address address) throws MessagingException {
        if (address instanceof InternetAddress) {
            InternetAddress ia = (InternetAddress) address;
            return ia.getPersonal() + " <" + ia.getAddress() + ">";
        } else {
            return address.toString();
        }
    }

    public static String getMailAddress(Address address) throws MessagingException {
        if (address instanceof InternetAddress) {
            InternetAddress ia = (InternetAddress) address;
            return  ia.getAddress();
        } else {
            return address.toString();
        }
    }

    public static Long getNextUid(Message m) throws MessagingException {
        Folder f = m.getFolder();
        if (f instanceof IMAPFolder) {
            return ((IMAPFolder) f).getUIDNext();
        }
        return null;
    }

    public static String getPlainContent(MimeMessage message) throws Exception {
        return new MimeMessageParser(message).parse().getPlainContent();
    }

    public static CharSequence getPlainContent(Object content) throws Exception {
        if (content instanceof String) return (String)content;
        if (content instanceof MimeMessage) return getPlainContent((MimeMessage) content);
        return String.valueOf(content);
    }
}

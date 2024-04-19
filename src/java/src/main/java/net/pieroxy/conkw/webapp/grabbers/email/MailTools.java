package net.pieroxy.conkw.webapp.grabbers.email;

import com.sun.mail.imap.IMAPFolder;
import org.apache.commons.mail.util.MimeMessageParser;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;

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
        if (content instanceof MimeMultipart) return getTextFromMimeMultipart((MimeMultipart) content);
        return String.valueOf(content);
    }

    private static String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart)  throws MessagingException, IOException {
        String result = "";
        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                return result + "\n" + bodyPart.getContent(); // without return, same text appears twice in my tests
            }
            result += parseBodyPart(bodyPart);
        }
        return result;
    }

    private static String parseBodyPart(BodyPart bodyPart) throws MessagingException, IOException {
        if (bodyPart.isMimeType("text/html")) {
            return "\n" + bodyPart.getContent().toString();
        }
        if (bodyPart.getContent() instanceof MimeMultipart){
            return getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
        }

        return "";
    }
}

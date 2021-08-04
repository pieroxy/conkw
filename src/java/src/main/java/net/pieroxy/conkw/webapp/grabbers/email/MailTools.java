package net.pieroxy.conkw.webapp.grabbers.email;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

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
}

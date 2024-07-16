package net.pieroxy.conkw.utils.hashing;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Sum {
    MessageDigest md;

    public Md5Sum() {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public Md5Sum add(String part) {
        if (part != null)
            md.update(part.getBytes(StandardCharsets.UTF_8));
        return this;
    }

    public Md5Sum add(Hashable part) {
        part.addToHash(this);
        return this;
    }

    public String getMd5Sum() {
        return HashTools.byteArrayToHexString(md.digest());
    }
}

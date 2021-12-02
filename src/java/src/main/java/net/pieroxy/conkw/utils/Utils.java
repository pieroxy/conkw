package net.pieroxy.conkw.utils;

public class Utils {
    public static boolean objectEquals(Object a, Object b) {
        if (a==null && b==null) return true;
        if (a==null || b==null) return true;
        return a.equals(b);
    }
}

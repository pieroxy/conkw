package net.pieroxy.conkw.accumulators;

import net.pieroxy.conkw.utils.StringUtil;

public class AccumulatorUtils {
    public static String addToMetricName(String prefix, String...toAdd) {
        StringBuilder res = new StringBuilder(prefix);
        for (String s : toAdd) {
            res.append('.').append(cleanMetricPathElement(s));
        }
        if (StringUtil.isNullOrEmptyTrimmed(prefix)) return res.substring(1);
        return res.toString();
    }

    public static String cleanMetricPathElement(String s) {
        if (s == null) return "null";
        return s.replaceAll("_", "__")
                .replaceAll(",", "_c")
                .replaceAll("\\.", "_d")
                ;
    }

    public static String parseMetricPathElement(String s) {
        if (s.indexOf("_")==-1) return s;
        StringBuilder sb = new StringBuilder(s.length());
        int status=0;
        for (int i=0 ; i<s.length() ; i++) {
            char c = s.charAt(i);
            switch (status) {
                case 0:
                    if (c == '_') {
                        status=1;
                    } else {
                        sb.append(c);
                    }
                    break;
                case 1:
                    switch (c) {
                        case '_':
                            sb.append("_");
                            break;
                        case 'c':
                            sb.append(",");
                            break;
                        case 'd':
                            sb.append(".");
                            break;
                        default:
                            throw new RuntimeException("Unknown sequence _" + c);
                    }
                    status=0;
            }
        }
        return sb.toString();
    }
}

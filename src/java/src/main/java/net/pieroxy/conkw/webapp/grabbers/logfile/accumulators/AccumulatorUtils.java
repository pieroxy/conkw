package net.pieroxy.conkw.webapp.grabbers.logfile.accumulators;

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
        return s.replaceAll("\\.", "_")
                .replaceAll(" ", "_")
                .replaceAll("=", "-")
                .replaceAll("\"", "'");
    }
}

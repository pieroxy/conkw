package net.pieroxy.conkw.accumulators.parser;

public class ParseException extends RuntimeException {
    private String originalMessage;
    private int position;

    public ParseException(String msg, String expr, int[] index) {
        super(getMessage(msg, expr, index));
        originalMessage = msg;
        position = index[0];
    }

    public ParseException(String msg, String expr, int[] index, Exception e) {
        super(getMessage(msg, expr, index), e);
        originalMessage = msg;
        position = index[0];
    }

    static String getMessage(String msg, String expr, int[] index) {
        return msg + "\r\n" + expr + "\r\n" + getIndexStr(index[0]) + (index[0] < expr.length() ? ("  : " + expr.charAt(index[0])) : "");
    }

    private static String getIndexStr(int index) {
        StringBuilder sb = new StringBuilder(index + 1);
        while (index-- > 0) {
            sb.append("-");
        }
        return sb.append("^").toString();
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

    public int getPosition() {
        return position;
    }
}

package net.pieroxy.conkw.utils.prefixeddata;

import net.pieroxy.conkw.pub.mdlog.DataRecord;

public interface PrefixedDataRecord extends DataRecord {
    void pushPrefix(String prefix);
    String popPrefix();
}

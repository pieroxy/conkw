package net.pieroxy.conkw.accumulators.parser;

import net.pieroxy.conkw.accumulators.Accumulator;
import net.pieroxy.conkw.pub.mdlog.DataRecord;

import java.lang.reflect.Constructor;
import java.util.List;

class TypedConstructor {
    List<ParamType> params;
    Constructor<Accumulator<DataRecord>> c;
}

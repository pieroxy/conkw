package net.pieroxy.conkw.accumulators.parser;

import net.pieroxy.conkw.accumulators.Accumulator;
import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;

import java.lang.reflect.Constructor;
import java.util.List;

class TypedConstructor {
    List<ParamType> params;
    Constructor<Accumulator<LogRecord>> c;
}

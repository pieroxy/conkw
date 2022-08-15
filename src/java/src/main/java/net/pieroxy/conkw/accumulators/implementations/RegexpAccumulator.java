package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.accumulators.Accumulator;
import net.pieroxy.conkw.pub.mdlog.DataRecord;
import net.pieroxy.conkw.pub.mdlog.GenericLogRecord;
import net.pieroxy.conkw.utils.prefixeddata.PrefixedDataRecord;

import java.util.regex.Pattern;

public class RegexpAccumulator implements Accumulator<DataRecord> {
    public static final String NAME = "regexpMultiValue";

    private final Pattern pattern;
    private final Accumulator<DataRecord> accumulator;

    public RegexpAccumulator(Pattern pattern, Accumulator<DataRecord> accumulator) {
        this.pattern = pattern;
        this.accumulator = accumulator;
    }

    @Override
    public double add(DataRecord line) {
        DataRecord tmpLine = new GenericLogRecord();
        return line
                .getValues()
                .keySet()
                .stream()
                .filter(s -> pattern.matcher(s).matches())
                .map(key -> {
                    tmpLine.getValues().clear();
                    tmpLine.getValues().put("value", line.getValues().get(key));
                    accumulator.add(tmpLine);
                    return 1;
                }).count();
    }

    @Override
    public void sumWith(Accumulator<DataRecord> acc) {
        if (acc instanceof RegexpAccumulator) {
            ((RegexpAccumulator) acc).accumulator.sumWith(accumulator);
        }
    }

    @Override
    public void initializeFromData(PrefixedDataRecord record) {
        accumulator.initializeFromData(record);
    }

    @Override
    public double getTotal() {
        return 0;
    }

    @Override
    public void prepareNewSession() {
        accumulator.prepareNewSession();
    }

    @Override
    public void log(String prefix, DataRecord record) {
        accumulator.log(prefix, record);
    }

    @Override
    public Accumulator<DataRecord> getFreshInstance() {
        return new RegexpAccumulator(pattern, accumulator.getFreshInstance());
    }

    public Pattern getPattern() {
        return pattern;
    }
}

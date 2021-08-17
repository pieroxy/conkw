package net.pieroxy.conkw.webapp.grabbers;

import net.pieroxy.conkw.utils.PerformanceTools;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class HwmonGrabber extends AsyncGrabber {
    static final String NAME = "hwmon";

    private static FileObject root = null;;
    private static final byte[]buffer = new byte[100];
    private Set<Pattern> include;
    private Map<String, StringBuilder> categories;


    @Override
    public boolean changed() {
        return true;
    }

    private void init() {
        if (root!=null) return;
        long start = System.currentTimeMillis();
        root = new FileObject(new File("/sys/class/hwmon/"));
        String[]mons = root.file.list();
        categories = new HashMap<>();


        Arrays.stream(mons).forEach(m -> {
            if (shouldExtract(m)) {
                FileObject actual = root.getChildren(m);
                String name;
                try {
                    name = PerformanceTools.readAllAsString(new File(actual.file, "name"), buffer).trim();
                } catch (IOException e) {
                    name = "?";
                }
                if (canLogFiner()) log(Level.FINER, "Init found " + m + " // " + name);
                String[]files = actual.file.list();
                for (String f : files) {
                    if (canLogFiner()) log(Level.FINE, "Init found data file " + f);
                    if (f.indexOf('_')>0 && shouldExtractMetric(f)) {
                        FileObject metricFile = actual.getChildren(f);
                        metricFile.metricName = (m.trim() + "_" + name + "_" + f).replaceAll(",", ".");
                        if (canLogFiner()) log(Level.FINER, "Metric name is " + metricFile.metricName + " for fo " + metricFile);
                        if (f.endsWith("_label")) {
                        } else {
                            String cat = getCategory(f);
                            StringBuilder sbcat = categories.get(cat);
                            if (sbcat == null) {
                                sbcat = new StringBuilder();
                                categories.put(cat, sbcat);
                            }
                            if (sbcat.length()>0) sbcat.append(",");
                            sbcat.append(metricFile.metricName);
                            metricFile.category = cat;
                        }
                    }
                }
            }
        });
        if (canLogFine()) log(Level.FINE, "Initialized hwmon in " + (System.currentTimeMillis() - start) + "ms");
    }

    @Override
    public ResponseData grabSync() {
        init();
        ResponseData res = new ResponseData(this, System.currentTimeMillis());
        try {
            root.children.entrySet().forEach(hwmon -> {
                if (canLogFiner()) log(Level.FINER, "Starting analysis of  " + hwmon.getKey());
                for (Map.Entry<String, FileObject> datafile : hwmon.getValue().children.entrySet()) {
                    try {
                        if (canLogFiner()) log(Level.FINER, "Going for data file " + datafile.getKey());
                        if (datafile.getKey().endsWith("_label")) {
                            String label = PerformanceTools.readAllAsString(datafile.getValue().file, buffer);
                            res.addMetric(datafile.getValue().metricName, label);
                        } else {
                            File data = datafile.getValue().file;
                            long value = PerformanceTools.parseLongInFile(data, buffer);
                            addMetric(res, datafile.getValue().category, datafile.getValue().metricName, datafile.getKey(), value);
                        }
                    } catch (Exception e) {
                        log(Level.INFO, "", e);
                    }
                }
            });
        } catch (Exception e) {
            log(Level.SEVERE, "", e);
        }

        for (Map.Entry<String, StringBuilder> e : categories.entrySet()) {
            res.addMetric(e.getKey(), e.getValue().toString());
        }
        return res;
    }

    private boolean shouldExtractMetric(String f) {
        if (include == null) return true;
        for (Pattern p : include) {
            if (p.matcher(f).matches()) {
                if (canLogFiner()) log(Level.FINER, p + " matches " + f);
                return true;
            } else {
                if (canLogFiner()) log(Level.FINER, p + " !matches " + f);
            }
        }
        return false;
    }

    private void addMetric(ResponseData res, String cat, String fullMetricName, String metricName, double value) {
        value = applyCatMult(cat, value);
        computeAutoMaxMinAbsolute(res, fullMetricName, value);
    }

    private double applyCatMult(String cat, double value) {
        switch (cat) {
            case "in_min":
            case "in_lcrit":
            case "in_max":
            case "in_crit":
            case "in_input":
            case "in_average":
            case "in_lowest":
            case "in_highest":
            case "cpu_vid":
            case "temp_max":
            case "temp_min":
            case "temp_max_hyst":
            case "temp_min_hyst":
            case "temp_input":
            case "temp_crit":
            case "temp_crit_hyst":
            case "temp_emergency":
            case "temp_emergency_hyst":
            case "temp_lcrit":
            case "temp_lcrit_hyst":
            case "temp_offset":
            case "temp_lowest":
            case "temp_highest":
            case "curr_max":
            case "curr_min":
            case "curr_lcrit":
            case "curr_crit":
            case "curr_input":
            case "curr_average":
            case "curr_lowest":
            case "curr_highest":
                return value / 1000;
            case "power_average":
            case "power_average_highest":
            case "power_average_lowest":
            case "power_average_max":
            case "power_average_min":
            case "power_input":
            case "power_input_highest":
            case "power_input_lowest":
            case "power_cap":
            case "power_cap_hyst":
            case "power_cap_max":
            case "power_cap_min":
            case "power_max":
            case "power_crit":
                return value / 1000000;
        }

        return value;
    }

    private String getCategory(String metricName) {
        int up = metricName.indexOf("_");
        if (up<1) return "misc";
        StringBuilder sb = new StringBuilder(metricName.length());
        for (int i=0 ; i<up ; i++) {
            char c = metricName.charAt(i);
            if (!Character.isDigit(c)) sb.append(c);
        }
        sb.append(metricName.substring(up));
        return sb.toString();
    }

    @Override
    public String getDefaultName() {
        return NAME;
    }

    @Override
    protected void setConfig(Map<String, String> config, Map<String, Map<String, String>> configs) {
        String includeString = config.get("include");

        if (includeString != null) {
            include = new HashSet<>();
            for (String s : includeString.split(",")) {
                include.add(Pattern.compile(s));
            }
        }
    }

    private static class FileObject {
        public FileObject(File h) {
            this.file = h;
            children = new TreeMap<>();
        }

        File file;
        String metricName;
        String category;
        Map<String, FileObject> children;

        public FileObject getChildren(String m) {
            FileObject res = children.get(m);
            if (res == null) children.put(m, res = new FileObject(new File(file, m)));
            return res;
        }
    }
}
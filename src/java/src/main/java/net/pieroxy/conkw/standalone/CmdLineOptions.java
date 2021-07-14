package net.pieroxy.conkw.standalone;

import java.util.Arrays;
import java.util.LinkedList;

public class CmdLineOptions {
    public static final String ACTION_START = "start";
    public static final String ACTION_STOP = "stop";
    public static final String ACTION_INSTALL = "install";


    private String action="";
    private String home;
    private boolean upgrade;
    private boolean overrideConfig;
    private boolean overrideUi;
    private boolean stopCurrentInstance;

    public CmdLineOptions(String[]argssa) {
        if (argssa.length == 0) return;
        LinkedList<String> args = new LinkedList<>();
        args.addAll(Arrays.asList(argssa));
        action = args.pop();

        while (!args.isEmpty()) {
            String cur = args.pop();
            switch (cur) {
                case "--override-default-ui":
                    overrideUi = true;
                    break;
                case "--upgrade":
                    upgrade = true;
                    break;
                case "--override-config-files":
                    overrideConfig = true;
                    break;
                case "--home":
                    home = args.pop();
                    break;
                case "--stop-current-instance":
                    stopCurrentInstance = true;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown argument provided: " + cur);
            }
        }
    }

    public String getAction() {
        return action;
    }

    public String getHome() {
        return home;
    }

    public boolean isOverrideConfig() {
        return overrideConfig;
    }

    public boolean isOverrideUi() {
        return overrideUi;
    }

    public boolean isStopCurrentInstance() {
        return stopCurrentInstance;
    }

    public boolean isUpgrade() {
        return upgrade;
    }
}

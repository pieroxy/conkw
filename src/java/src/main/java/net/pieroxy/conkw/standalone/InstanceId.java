package net.pieroxy.conkw.standalone;

import com.dslplatform.json.CompiledJson;

@CompiledJson(onUnknown = CompiledJson.Behavior.FAIL)
public class InstanceId {
    String pid;
    String key;

    public InstanceId(String pid, String key) {
        this.pid = pid;
        this.key = key;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

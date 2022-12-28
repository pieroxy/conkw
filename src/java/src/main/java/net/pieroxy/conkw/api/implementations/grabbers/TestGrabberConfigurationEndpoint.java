package net.pieroxy.conkw.api.implementations.grabbers;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.NonNull;
import net.pieroxy.conkw.api.metadata.AbstractApiEndpoint;
import net.pieroxy.conkw.api.metadata.ApiMethod;
import net.pieroxy.conkw.api.metadata.Endpoint;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.metadata.grabberConfig.GrabberConfigMessage;
import net.pieroxy.conkw.api.model.User;
import net.pieroxy.conkw.config.UserRole;
import net.pieroxy.conkw.grabbersBase.Grabber;
import net.pieroxy.conkw.utils.config.GrabberConfigReader;

import java.util.List;

@Endpoint(
        method = ApiMethod.POST,
        role = UserRole.ADMIN)
public class TestGrabberConfigurationEndpoint extends AbstractApiEndpoint<TestGrabberConfigurationInput, TestGrabberConfigurationOutput> {
    @Override
    public TestGrabberConfigurationOutput process(TestGrabberConfigurationInput input, User user) throws Exception {
        Grabber grabber = (Grabber)Class.forName(input.getGrabberImplementation()).newInstance();
        Object config = GrabberConfigReader.fillObject(grabber.getDefaultConfig(), input.getConfiguration().getConfig());
        TestGrabberConfigurationOutput result = new TestGrabberConfigurationOutput(grabber.validateConfiguration(config));
        if (!input.isForceSave()) {
            if (!result.getMessages().isEmpty()) {
                // We don't save if there are any message
                return result;
            }
        }
        if (result.getMessages()
                .stream()
                .filter(GrabberConfigMessage::isError)
                .count() > 0) {
            // We never save if there are errors reported
            return result;
        }
        result.setSaved(true);
        return result;
    }
}

@CompiledJson
@TypeScriptType
class TestGrabberConfigurationInput {
    @NonNull
    private String grabberImplementation;
    @NonNull
    private boolean forceSave;
    @NonNull
    private GrabberConfigDetail configuration;

    public String getGrabberImplementation() {
        return grabberImplementation;
    }

    public void setGrabberImplementation(String grabberImplementation) {
        this.grabberImplementation = grabberImplementation;
    }

    public GrabberConfigDetail getConfiguration() {
        return configuration;
    }

    public void setConfiguration(GrabberConfigDetail configuration) {
        this.configuration = configuration;
    }

    public boolean isForceSave() {
        return forceSave;
    }

    public void setForceSave(boolean forceSave) {
        this.forceSave = forceSave;
    }
}


@CompiledJson
@TypeScriptType
class TestGrabberConfigurationOutput {
    private List<GrabberConfigMessage> messages;
    @NonNull
    private boolean isSaved;

    public TestGrabberConfigurationOutput() {
    }

    public TestGrabberConfigurationOutput(List<GrabberConfigMessage> messages) {
        this.messages = messages;
    }

    public List<GrabberConfigMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<GrabberConfigMessage> messages) {
        this.messages = messages;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }
}
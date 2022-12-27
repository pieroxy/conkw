package net.pieroxy.conkw.api.implementations.grabbers;

import com.dslplatform.json.CompiledJson;
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
        return new TestGrabberConfigurationOutput(grabber.validateConfiguration(config));
    }
}

@CompiledJson
@TypeScriptType
class TestGrabberConfigurationInput {
    private String grabberImplementation;
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
}


@CompiledJson
@TypeScriptType
class TestGrabberConfigurationOutput {
    List<GrabberConfigMessage> messages;

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
}
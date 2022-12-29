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
import net.pieroxy.conkw.utils.Services;
import net.pieroxy.conkw.utils.Utils;
import net.pieroxy.conkw.utils.config.GrabberConfigReader;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

@Endpoint(
        method = ApiMethod.POST,
        role = UserRole.ADMIN)
public class TestGrabberConfigurationEndpoint extends AbstractApiEndpoint<TestGrabberConfigurationInput, TestGrabberConfigurationOutput> {

    private final Services services;

    public TestGrabberConfigurationEndpoint(Services services) {
        this.services = services;
    }

    @Override
    public TestGrabberConfigurationOutput process(TestGrabberConfigurationInput input, User user) throws Exception {
        Grabber grabber = (Grabber)Class.forName(input.getGrabberImplementation()).newInstance();
        Object config = GrabberConfigReader.fillObject(grabber.getDefaultConfig(), input.getConfiguration().getConfig());
        TestGrabberConfigurationOutput result = new TestGrabberConfigurationOutput(grabber.validateConfiguration(config));

        if (Utils.objectEquals(input.getConfiguration().getLogLevel(), Level.OFF.getName())) {
            result.getMessages().add(new GrabberConfigMessage(false, "logLevel", "No matter what happens with this grabber, nothing will end up in the logs."));
        }
        if (!Objects.equals(input.getGrabberName(), input.getConfiguration().getName())) {
            // Grabber name is about to change
            Grabber already = services.getGrabbersService().getGrabberByName(input.getConfiguration().getName());
            if (already != null) {
                result.getMessages().add(new GrabberConfigMessage(true, "name", "There is already a grabber named " + input.getConfiguration().getName() + ". Names must be unique."));
            }
        }
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
    private String grabberName;
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

    @NonNull
    public String getGrabberName() {
        return grabberName;
    }

    public void setGrabberName(@NonNull String grabberName) {
        this.grabberName = grabberName;
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
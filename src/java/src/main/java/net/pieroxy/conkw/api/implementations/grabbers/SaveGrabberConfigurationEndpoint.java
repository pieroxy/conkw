package net.pieroxy.conkw.api.implementations.grabbers;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.NonNull;
import net.pieroxy.conkw.api.metadata.AbstractApiEndpoint;
import net.pieroxy.conkw.api.metadata.ApiMethod;
import net.pieroxy.conkw.api.metadata.Endpoint;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.metadata.grabberConfig.GrabberConfigMessage;
import net.pieroxy.conkw.api.model.User;
import net.pieroxy.conkw.config.ConfigReader;
import net.pieroxy.conkw.config.GrabberConfig;
import net.pieroxy.conkw.config.UserRole;
import net.pieroxy.conkw.grabbersBase.Grabber;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.Services;
import net.pieroxy.conkw.utils.StringUtil;
import net.pieroxy.conkw.utils.Utils;
import net.pieroxy.conkw.utils.config.GrabberConfigReader;
import net.pieroxy.conkw.utils.config.ParsingError;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Endpoint(
        method = ApiMethod.POST,
        role = UserRole.ADMIN)
public class SaveGrabberConfigurationEndpoint extends AbstractApiEndpoint<SaveGrabberConfigurationInput, SaveGrabberConfigurationOutput> {
    private static Logger LOGGER = Logger.getLogger(SaveGrabberConfigurationEndpoint.class.getName());


    private final Services services;

    public SaveGrabberConfigurationEndpoint(Services services) {
        this.services = services;
    }

    @Override
    public SaveGrabberConfigurationOutput process(SaveGrabberConfigurationInput input, User user) throws Exception {
        Grabber grabber = (Grabber)Class.forName(input.getGrabberImplementation()).newInstance();
        List<ParsingError> errors = new ArrayList<>();
        Object config = GrabberConfigReader.fillObject(grabber.getDefaultConfig(), input.getConfiguration().getConfig(), errors);
        SaveGrabberConfigurationOutput result = new SaveGrabberConfigurationOutput(grabber.validateConfiguration(config));
        errors.forEach(e -> result.getMessages().add(new GrabberConfigMessage(true, e.getFieldName(), e.getMessage())));

        if (Utils.objectEquals(input.getConfiguration().getLogLevel(), Level.OFF.getName())) {
            result.getMessages().add(new GrabberConfigMessage(false, "logLevel", "No matter what happens with this grabber, nothing will end up in the logs."));
        }
        if (!Objects.equals(input.getGrabberName(), input.getConfiguration().getName())) {
            // Grabber name is about to change
            String newName = input.getConfiguration().getName();
            if (newName == null) newName = "";
            LOGGER.info("Logger is about to be renamed : '" + input.getGrabberName() + "' >> '" + newName + "'");
            if (newName.contains(",")) {
                result.getMessages().add(new GrabberConfigMessage(true, "name", "Grabber name cannot contain ',' (comma)."));
            }
            if (StringUtil.isNullOrEmptyTrimmed(newName)) {
                result.getMessages().add(new GrabberConfigMessage(true, "name", "Grabber name cannot be empty."));
            } else {
                Grabber already = services.getGrabbersService().getGrabberByName(newName);
                if (already != null) {
                    result.getMessages().add(new GrabberConfigMessage(true, "name", "There is already a grabber named " + newName + ". Names must be unique."));
                }
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
        saveGrabberConfig(input.getGrabberName(), grabber, input.getConfiguration());
        return result;
    }

    private void saveGrabberConfig(String grabberName, Grabber grabber, GrabberConfigDetail configuration) throws InstantiationException, IllegalAccessException {
        GrabberConfig config = new GrabberConfig();
        config.setName(configuration.getName());
        config.setDefaultAccumulator(configuration.getDefaultAccumulator());
        config.setLogLevel(configuration.getLogLevel());
        config.setImplementation(configuration.getImplementation());
        config.setConfig(configuration.getConfig());
        services.getGrabbersService().updateGrabberConfig(grabberName, config);
        ConfigReader.updateGrabberConfig(grabberName, config);
        services.setConfiguration(ConfigReader.getConfig());
    }
}

@CompiledJson
@TypeScriptType
class SaveGrabberConfigurationInput {
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
class SaveGrabberConfigurationOutput {
    private List<GrabberConfigMessage> messages;
    @NonNull
    private boolean isSaved;

    public SaveGrabberConfigurationOutput() {
    }

    public SaveGrabberConfigurationOutput(List<GrabberConfigMessage> messages) {
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
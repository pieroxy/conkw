package net.pieroxy.conkw.api.implementations.auth;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.AbstractApiEndpoint;
import net.pieroxy.conkw.api.metadata.ApiMethod;
import net.pieroxy.conkw.api.metadata.Endpoint;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.User;
import net.pieroxy.conkw.config.UserRole;
import net.pieroxy.conkw.services.UserSessionService;
import net.pieroxy.conkw.utils.Services;
import net.pieroxy.conkw.utils.exceptions.DisplayMessageException;

import java.util.logging.Logger;

@Endpoint(
        method = ApiMethod.POST,
        role = UserRole.VIEWER
)
public class RemoveSessionEndpoint extends AbstractApiEndpoint<RemoveSessionInput, Object> {
    private static final Logger LOGGER = Logger.getLogger(RemoveSessionEndpoint.class.getName());

    private final Services services;

    public RemoveSessionEndpoint(Services services) {
        this.services = services;
    }

    @Override
    public Object process(RemoveSessionInput input, User user) throws Exception {
        UserSessionService.Session session = services.getUserSessionService().getSession(input.getToken());
        if (session!=null) {
            if (session.getUserid().equals(user.getId())) {
                services.getUserSessionService().removeSession(input.getToken());
                return null;
            }
            else {
                LOGGER.warning("Session provided did not belong to the logged-in user: " + input.getToken() + " submitted by " + user);
            }
        } else {
            LOGGER.warning("Session provided did not match any session in database: " + input.getToken() + " submitted by " + user);
        }
        throw new DisplayMessageException("Invalid session provided");
    }
}


@CompiledJson
@TypeScriptType
class RemoveSessionInput {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
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

import java.util.List;

@Endpoint(
        method = ApiMethod.GET,
        role = UserRole.VIEWER
)
public class GetAllUserSessionsEndpoint extends AbstractApiEndpoint<Object, GetAllUserSessionsOutput> {
    private final Services services;

    public GetAllUserSessionsEndpoint(Services services) {
        this.services = services;
    }

    @Override
    public GetAllUserSessionsOutput process(Object input, User user) throws Exception {
        return new GetAllUserSessionsOutput(services.getUserSessionService().getSessions(user));
    }
}

@CompiledJson
@TypeScriptType
class GetAllUserSessionsOutput {
    private List<UserSessionService.Session> sessions;

    public GetAllUserSessionsOutput(List<UserSessionService.Session> sessions) {
        this.sessions = sessions;
    }

    public GetAllUserSessionsOutput() {
    }

    public List<UserSessionService.Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<UserSessionService.Session> sessions) {
        this.sessions = sessions;
    }
}
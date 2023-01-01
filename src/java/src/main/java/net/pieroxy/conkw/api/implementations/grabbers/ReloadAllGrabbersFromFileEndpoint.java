package net.pieroxy.conkw.api.implementations.grabbers;

import net.pieroxy.conkw.api.metadata.AbstractApiEndpoint;
import net.pieroxy.conkw.api.metadata.ApiMethod;
import net.pieroxy.conkw.api.metadata.Endpoint;
import net.pieroxy.conkw.api.model.User;
import net.pieroxy.conkw.config.UserRole;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.Services;

@Endpoint(
        method = ApiMethod.POST,
        role = UserRole.ADMIN)
public class ReloadAllGrabbersFromFileEndpoint extends AbstractApiEndpoint<JsonHelper.EmptyObject, JsonHelper.EmptyObject> {
    private final Services services;

    public ReloadAllGrabbersFromFileEndpoint(Services services) {
        this.services = services;
    }

    @Override
    public JsonHelper.EmptyObject process(JsonHelper.EmptyObject input, User user) throws Exception {
        services.getGrabbersService().loadConfig();
        return new JsonHelper.EmptyObject();
    }
}

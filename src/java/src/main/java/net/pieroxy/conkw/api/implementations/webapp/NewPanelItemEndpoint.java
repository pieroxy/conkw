package net.pieroxy.conkw.api.implementations.webapp;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.AbstractApiEndpoint;
import net.pieroxy.conkw.api.metadata.ApiMethod;
import net.pieroxy.conkw.api.metadata.Endpoint;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.Dashboard;
import net.pieroxy.conkw.api.model.User;
import net.pieroxy.conkw.api.model.panels.DashboardPanel;
import net.pieroxy.conkw.api.model.panels.toplevel.GaugeWithHistoryElement;
import net.pieroxy.conkw.api.model.panels.toplevel.LabelAndValueElement;
import net.pieroxy.conkw.api.model.panels.toplevel.SimpleGaugeWithValueAndLabelElement;
import net.pieroxy.conkw.api.model.panels.atoms.model.TopLevelPanelElement;
import net.pieroxy.conkw.api.model.panels.atoms.model.TopLevelPanelElementEnum;
import net.pieroxy.conkw.config.UserRole;
import net.pieroxy.conkw.utils.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Endpoint(
        method = ApiMethod.POST,
        role = UserRole.DESIGNER)
public class NewPanelItemEndpoint extends AbstractApiEndpoint<NewPanelItemInput, NewPanelItemOutput> {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private final Services services;

    public NewPanelItemEndpoint(Services services) {
        this.services = services;
    }

    @Override
    public NewPanelItemOutput process(NewPanelItemInput input, User user) throws Exception {
        Dashboard dashboard = services.getDashboardService().getDashboardById(input.getDashboardId());
        if (dashboard == null) {
            LOGGER.warning("Dashboard " + input.getDashboardId() + " not found");
            throw new RuntimeException("Dashboard " + input.getDashboardId() + " not found");
        }
        if (dashboard.getPanels()==null) dashboard.setPanels(new ArrayList<>());
        List<DashboardPanel> panels = dashboard.getPanels().stream().filter(p -> p.getId().equals(input.getPanelId())).collect(Collectors.toList());
        if (panels.size() == 0) {
            LOGGER.warning("Panel " + input.getPanelId() + " on dashboard " + input.getDashboardId() + " not found");
            throw new RuntimeException("Panel " + input.getPanelId() + " on dashboard " + input.getDashboardId() + " not found");
        }
        DashboardPanel panel = panels.get(0);

        switch (input.getType()) {
            case SIMPLE_GAUGE:
                return new NewPanelItemOutput(new SimpleGaugeWithValueAndLabelElement());
            case GAUGE_WITH_HISTORY:
                return new NewPanelItemOutput(new GaugeWithHistoryElement());
            case LABEL_VALUE:
                return new NewPanelItemOutput(new LabelAndValueElement());
            default:
                throw new RuntimeException("Item of type " + input.getType() + " not implemented yet");
        }
    }
}


@CompiledJson
@TypeScriptType
class NewPanelItemInput {
    private String dashboardId;
    private String panelId;
    private TopLevelPanelElementEnum type;

    public String getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(String dashboardId) {
        this.dashboardId = dashboardId;
    }

    public String getPanelId() {
        return panelId;
    }

    public void setPanelId(String panelId) {
        this.panelId = panelId;
    }

    public TopLevelPanelElementEnum getType() {
        return type;
    }

    public void setType(TopLevelPanelElementEnum type) {
        this.type = type;
    }
}

@CompiledJson
@TypeScriptType
class NewPanelItemOutput {
    TopLevelPanelElement element;

    public NewPanelItemOutput(TopLevelPanelElement element) {
        this.element = element;
    }

    public NewPanelItemOutput() {
    }

    public TopLevelPanelElement getElement() {
        return element;
    }

    public void setElement(TopLevelPanelElement element) {
        this.element = element;
    }
}

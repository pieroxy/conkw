package net.pieroxy.conkw.api.implementations.dashboards;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.AbstractApiEndpoint;
import net.pieroxy.conkw.api.metadata.ApiMethod;
import net.pieroxy.conkw.api.metadata.Endpoint;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.Dashboard;
import net.pieroxy.conkw.api.model.User;
import net.pieroxy.conkw.api.model.panels.DashboardPanel;
import net.pieroxy.conkw.api.model.panels.GaugeWithHistoryElement;
import net.pieroxy.conkw.api.model.panels.SimpleGaugeWithValueAndLabelElement;
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
public class NewPanelItemEndpoint extends AbstractApiEndpoint<NewPanelItemInput, Object> {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private final Services services;

    public NewPanelItemEndpoint(Services services) {
        this.services = services;
    }

    @Override
    public Object process(NewPanelItemInput input, User user) throws Exception {
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

        String newId = null;
        switch (input.getType()) {
            case SIMPLE_GAUGE: {
                SimpleGaugeWithValueAndLabelElement elem = new SimpleGaugeWithValueAndLabelElement();
                elem.initialize();
                panel.addContent(elem);
                services.getDashboardService().saveDashboard(dashboard);
                break;
            }
            case GAUGE_WITH_HISTORY:
            {
                GaugeWithHistoryElement elem = new GaugeWithHistoryElement();
                elem.initialize();
                panel.addContent(elem);
                services.getDashboardService().saveDashboard(dashboard);
                break;
            }
            default:
                throw new RuntimeException("Item of type " + input.getType() + " not implemented yet");
        }
        return null;
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

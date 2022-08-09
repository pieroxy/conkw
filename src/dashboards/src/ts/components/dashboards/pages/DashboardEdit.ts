import m from 'mithril';
import { Dashboard } from '../../../auto/pieroxy-conkw';
import { IdAttrs } from '../../../utils/attrs/IdAttrs';
import { HomeIcon } from '../../atoms/icons/HomeIcon';
import { RightChevronIcon } from '../../atoms/icons/RightChevronIcon';
import { Endpoints } from '../../../utils/navigation/Endpoints';
import { Routing } from '../../../utils/navigation/Routing';
import { AbstractPage } from '../../pages/AbstractPage';
import { NewDocumentIcon } from '../../atoms/icons/NewDocumentIcon';
import { GlobalData } from '../../../utils/GlobalData';
import { ApiEndpoints } from '../../../auto/ApiEndpoints';
import { Link } from '../../atoms/Link';
import { ViewDashboardPanel } from '../../panels/ViewDashboardPanel';

export class DashboardEditPage extends AbstractPage<IdAttrs> {
  private dashboard : Dashboard;

  oninit({attrs}:m.Vnode<IdAttrs>) {
    ApiEndpoints.GetDashboard.call({
        id:attrs.id
      }
    ).then((response) => {
      this.dashboard = response.dashboard;
      GlobalData.setDashboardTitle(this.dashboard.id, this.dashboard.name);
    })
  }

  getPageTitle(): string {
    return this.dashboard?this.dashboard.name:"Loading ...";
  }

  render({attrs}:m.Vnode<IdAttrs>):m.Children {
    if (!this.dashboard) return m("");
    return m(".page", [
      m(".title", [
        m(Link, {className:"floatright", tooltip:"New panel", target:Routing.getRoute(Endpoints.DASHBOARD_PANEL_EDIT, {dashboardId:this.dashboard.id, panelId:0})}, m(NewDocumentIcon)),
        m(Link, {tooltip:"Go back Home", target:Endpoints.HOME}, m(HomeIcon)),
        m(RightChevronIcon),
        this.dashboard.name, 
        m(".id", this.dashboard.id)
      ]),
      m(".content.dashboardContent", this.dashboard.panels?this.dashboard.panels.map((panel) => 
          m(ViewDashboardPanel, {
            element:panel,
            editLink:Routing.getRoute(Endpoints.DASHBOARD_PANEL_EDIT, {dashboardId:attrs.id, panelId:panel.id})
          }))
        :null
      )
    ]);
  }
}


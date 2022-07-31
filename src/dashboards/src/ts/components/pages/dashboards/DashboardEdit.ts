import m from 'mithril';
import { Dashboard, GetDashboardInput, GetDashboardOutput, SimpleGaugeWithValueAndLabelPanel } from '../../../auto/pieroxy-conkw';
import { Api } from '../../../utils/api/Api';
import { IdAttrs } from '../../../utils/attrs/IdAttrs';
import { HomeIcon } from '../../icons/HomeIcon';
import { RightChevronIcon } from '../../icons/RightChevronIcon';
import { Endpoints } from '../../../utils/navigation/Endpoints';
import { Routing } from '../../../utils/navigation/Routing';
import { AbstractPage } from '../AbstractPage';
import { NewDocumentIcon } from '../../icons/NewDocumentIcon';
import { GlobalData } from '../../../utils/GlobalData';
import { ViewSimpleGaugeWithValueAndLabelPanel } from '../../panels/dashboards/SimpleGaugeWithValueAndLabelPanel';

export class DashboardEditPage extends AbstractPage<IdAttrs> {
  private dashboard : Dashboard;

  oninit({attrs}:m.Vnode<IdAttrs>) {
    Api.call<GetDashboardInput, GetDashboardOutput>({
      method:"GET",
      endpoint:"GetDashboard",
      body:{
        id:attrs.id
      }
    }).then((response) => {
      this.dashboard = response.dashboard;
      GlobalData.setDashboardTitle(this.dashboard.id, this.dashboard.name);
    })
  }

  getPageTitle(): string {
    return this.dashboard?this.dashboard.name:"Loading ...";
  }

  render({attrs}:m.Vnode<IdAttrs>):m.Children {
    if (!this.dashboard) return m("");
    return m(".dashboard.page", [
      m(".title", [
        m("a.floatright", {title:"New panel", href:Routing.getRouteAsHref(Endpoints.PANEL_NEW, {dashboardId:this.dashboard.id})}, m(NewDocumentIcon)),
        m("a", {title:"Go back Home", href:Routing.getRouteAsHref(Endpoints.HOME)}, m(HomeIcon)),
        m(RightChevronIcon),
        this.dashboard.name, 
        m(".id", this.dashboard.id)
      ]),
      m(".dashboardContent", this.dashboard.panels?this.dashboard.panels.map((panel) => 
        m(ViewSimpleGaugeWithValueAndLabelPanel, {
          element:<SimpleGaugeWithValueAndLabelPanel>panel,
          editLink:Routing.getRouteAsHref(Endpoints.GAUGE_SIMPLE_VALUE_LABEL_EDIT, {dashboardId:attrs.id, panelId:panel.id})
        }))
      :null)
    ]);
  }
}


import m from 'mithril';
import { Dashboard, GetDashboardInput, GetDashboardOutput } from '../../../auto/pieroxy-conkw';
import { Api } from '../../../utils/api/Api';
import { IdAttrs } from '../../../utils/attrs/IdAttrs';
import { HomeIcon } from '../../../utils/icons/HomeIcon';
import { RightChevronIcon } from '../../../utils/icons/RightChevronIcon';
import { Endpoints } from '../../../utils/navigation/Endpoints';
import { Routing } from '../../../utils/navigation/Routing';
import { AbstractPage } from '../AbstractPage';

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
    })
  }

  getPageTitle(): string {
    return this.dashboard?this.dashboard.name:"Loading ...";
  }
  render(_vnode:m.Vnode<any, any>):m.Children {
    if (!this.dashboard) return m("");
    return m(".dashboard.page", [
      m(".title", [m("a", {href:Routing.getRouteAsHref(Endpoints.HOME)}, m(HomeIcon)), m(RightChevronIcon), this.dashboard.name, m(".id", this.dashboard.id)]),
    ]);
  }
}


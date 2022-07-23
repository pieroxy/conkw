import m from 'mithril';
import { Dashboard, GetDashboardInput, GetDashboardOutput } from '../../../auto/pieroxy-conkw';
import { Api } from '../../../utils/api/Api';
import { IdAttrs } from '../../../utils/attrs/IdAttrs';
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
      m(".title", [this.dashboard.name, m(".id", this.dashboard.id)]),
    ]);
  }
}


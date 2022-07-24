import m from 'mithril';

import { Children } from "mithril";
import { GetDashboardsInput, GetDashboardsOutput } from '../../auto/pieroxy-conkw';
import { Api } from '../../utils/api/Api';
import { Endpoints } from '../../utils/navigation/Endpoints';
import { Routing } from '../../utils/navigation/Routing';
import { TextFilter } from '../../utils/TextFilter';
import { DashboardExplorerToolbar } from '../toolbars/DashboardExplorerToolbar';

export class DashboardExplorer implements m.ClassComponent<DashboardExplorerAttrs> {
  filter:TextFilter = new TextFilter("");
  data:GetDashboardsOutput = {list:[]}

  oninit() {
    this.load();
  }

  load() {
    Api.call<GetDashboardsInput, GetDashboardsOutput>({
      method:"GET",
      endpoint:"GetDashboards",
      body:{
        root:""
      }
    }).then((data) => {
      this.data = data;
    })
  }
  
  view({}: m.Vnode<DashboardExplorerAttrs>): void | Children {
    return [
        m(DashboardExplorerToolbar, {
          filter:(s:string) => {
            this.filter.update(s);
          },
          reload:() => this.load()
        }),
        m(".content", m(".list", [
          this.data.list.map(e => this.filter.matchOne([e.name]) ? m("a.listitem", {onclick:() => Routing.goToScreen(Endpoints.DASHBOARD_EDITION, {id:e.id})}, e.name) : null)
        ]))
      ];
  }
}

export interface DashboardExplorerAttrs {
}
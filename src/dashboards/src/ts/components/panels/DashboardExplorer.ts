import m from 'mithril';

import { Children } from "mithril";
import { GetDashboardsOutput } from '../../auto/pieroxy-conkw';
import { TextFilter } from '../../utils/TextFilter';
import { DashboardExplorerToolbar } from '../toolbars/DashboardExplorerToolbar';

export class DashboardExplorer implements m.ClassComponent<DashboardExplorerAttrs> {
  filter:TextFilter = new TextFilter("");
  
  view({attrs}: m.Vnode<DashboardExplorerAttrs>): void | Children {
    return m(
      ".dashboardExplorer", 
      [
        m(DashboardExplorerToolbar, {
          filter:(s:string) => {
            this.filter.update(s);
          }
        }),
        m(".list", [
          attrs.dashboards?.list.map(e => this.filter.matchOne([e.name]) ? m("", e.name) : null)
        ])
      ]);
  }
}

export interface DashboardExplorerAttrs {
  dashboards?:GetDashboardsOutput;
}
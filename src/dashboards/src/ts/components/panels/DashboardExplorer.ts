import m from 'mithril';

import { Children } from "mithril";
import { GetDashboardsOutput } from '../../auto/pieroxy-conkw';
import { DashboardExplorerToolbar } from '../toolbars/DashboardExplorerToolbar';

export class DashboardExplorer implements m.ClassComponent<DashboardExplorerAttrs> {
  
  view({attrs}: m.Vnode<DashboardExplorerAttrs>): void | Children {
    return m(
      ".dashboardExplorer", 
      [
        m(DashboardExplorerToolbar),
        m(".list", [
          attrs.dashboards?.list.map(e => m("", e.name))
        ])
      ]);
  }
}

export interface DashboardExplorerAttrs {
  dashboards?:GetDashboardsOutput;
}
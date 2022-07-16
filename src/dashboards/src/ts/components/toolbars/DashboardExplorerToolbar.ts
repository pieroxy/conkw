import m from 'mithril';

import { Children } from "mithril";
import { Button } from '../forms/Button';

export class DashboardExplorerToolbar implements m.ClassComponent<any> {
  
  view(): void | Children {
    return m(".toolbar", [
      m("input"),
      m(".right", 
        m(Button, {
          action:() => {}
        }, "+")
      )
    ])
  }
}
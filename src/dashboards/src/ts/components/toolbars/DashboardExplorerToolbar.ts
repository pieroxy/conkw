import m from 'mithril';

import { Children } from "mithril";
import { Button } from '../forms/Button';
import { Form } from '../forms/Form';
import { TextFieldInput } from '../forms/TextFieldInput';

export class DashboardExplorerToolbar implements m.ClassComponent<DashboardExplorerToolbarAttrs> {
  private filter:string="";
  
  view({attrs}:m.Vnode<DashboardExplorerToolbarAttrs>): void | Children {
    return m(".toolbar", [
      m(".inputlabel", "Search"),
      m(TextFieldInput, {
        refHolder:this,
        refProperty:"filter",
        form:new Form(),
        onchange:()=>attrs.filter(this.filter)
      }),
      m(".right", 
        m(Button, {
          action:() => {}
        }, "+")
      )
    ])
  }
}

export interface DashboardExplorerToolbarAttrs {
  filter: (s:string)=>void;
}
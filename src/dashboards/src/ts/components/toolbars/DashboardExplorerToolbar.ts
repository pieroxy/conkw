import m from 'mithril';

import { Children } from "mithril";
import { Dialogs } from '../../utils/Dialogs';
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
        onchange:()=>attrs.filter(this.filter),
        onenter:()=>{}
      }),
      m(".right", 
        m(Button, {
          action:() => {
            let go=(_s:string) => {
            };
            Dialogs.prompt("New dashboard name","",(s) => s,go);
          }
        }, "+")
      )
    ])
  }
}

export interface DashboardExplorerToolbarAttrs {
  filter: (s:string)=>void;
}
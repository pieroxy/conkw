import m from 'mithril';

import { Children } from "mithril";
import { CreateNewDashboardInput, CreateNewDashboardOutput } from '../../auto/pieroxy-conkw';
import { Api } from '../../utils/api/Api';
import { Dialogs } from '../../utils/Dialogs';
import { Button } from '../forms/Button';
import { Form } from '../forms/Form';
import { TextFieldInput } from '../forms/TextFieldInput';

export class DashboardExplorerToolbar implements m.ClassComponent<DashboardExplorerToolbarAttrs> {
  private filter:string="";
  
  view({attrs}:m.Vnode<DashboardExplorerToolbarAttrs>): void | Children {
    return m(".toolbar", [
      m(".right", 
        m(Button, {
          action:() => {
            let go=(s:string) => {
              Api.call<CreateNewDashboardInput, CreateNewDashboardOutput>({
                method:"POST",
                endpoint:"CreateNewDashboard",
                body:{
                  name:s
                }
            }).then(attrs.reload)
            };
            Dialogs.prompt("New dashboard name","",(s) => s,go);
          }
        }, "+")
      ),
      m(".title", "Dashboards"),
      m(TextFieldInput, {
        refHolder:this,
        refProperty:"filter",
        form:new Form(),
        onchange:()=>attrs.filter(this.filter),
        onenter:()=>{},
        search:true,
        placeholder:"Filter",
        className:".right.rm10"
      }),
    ])
  }
}

export interface DashboardExplorerToolbarAttrs {
  filter: (s:string)=>void;
  reload:()=>void;
}
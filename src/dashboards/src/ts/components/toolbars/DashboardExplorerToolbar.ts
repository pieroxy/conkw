import m from 'mithril';

import { Children } from "mithril";
import { CreateNewDashboardInput, CreateNewDashboardOutput } from '../../auto/pieroxy-conkw';
import { Api } from '../../utils/api/Api';
import { Dialogs } from '../../utils/Dialogs';
import { Form } from '../forms/Form';
import { TextFieldInput } from '../forms/TextFieldInput';
import { HomeIcon } from '../icons/HomeIcon';
import { RoundedPlusIcon } from '../icons/RoundedPlusIcon';

export class DashboardExplorerToolbar implements m.ClassComponent<DashboardExplorerToolbarAttrs> {
  private filter:string="";
  
  view({attrs}:m.Vnode<DashboardExplorerToolbarAttrs>): void | Children {
    return m(".title", [
      m(".floatright", 
        m("a", {
          title:"Create a new dashboard",
          onclick:() => {
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
        }, m(RoundedPlusIcon))
      ),
      m(TextFieldInput, {
        refHolder:this,
        refProperty:"filter",
        form:new Form(),
        onchange:()=>attrs.filter(this.filter),
        onenter:()=>{},
        search:true,
        placeholder:"Filter",
        className:".floatright.rm10",
        spellcheck:false
      }),
      m(HomeIcon),
      "Dashboards",
    ])
  }
}

export interface DashboardExplorerToolbarAttrs {
  filter: (s:string)=>void;
  reload:()=>void;
}
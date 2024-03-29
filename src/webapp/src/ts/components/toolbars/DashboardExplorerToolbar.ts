import m from 'mithril';

import { Children } from "mithril";
import { ApiEndpoints } from '../../auto/ApiEndpoints';
import { Dialogs } from '../../utils/Dialogs';
import { Form } from '../atoms/forms/Form';
import { TextFieldInput } from '../atoms/forms/TextFieldInput';
import { HomeIcon } from '../atoms/icons/HomeIcon';
import { RoundedPlusIcon } from '../atoms/icons/RoundedPlusIcon';

export class DashboardExplorerToolbar implements m.ClassComponent<DashboardExplorerToolbarAttrs> {
  private filter:string="";
  
  view({attrs}:m.Vnode<DashboardExplorerToolbarAttrs>): void | Children {
    return m(".title", [
      m(".floatright", 
        m("a", {
          title:"Create a new dashboard",
          onclick:() => {
            let go=(s:string) => {
              ApiEndpoints.CreateNewDashboard.call({
                  name:s
                }
              ).then(attrs.reload)
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
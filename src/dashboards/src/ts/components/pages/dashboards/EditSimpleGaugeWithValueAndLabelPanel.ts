import m from 'mithril';
import { HomeIcon } from '../../icons/HomeIcon';
import { RightChevronIcon } from '../../icons/RightChevronIcon';
import { Endpoints } from '../../../utils/navigation/Endpoints';
import { Routing } from '../../../utils/navigation/Routing';
import { AbstractPage } from '../AbstractPage';
import { GlobalData } from '../../../utils/GlobalData';
import { Api } from '../../../utils/api/Api';
import { GetDashboardPanelInput, GetDashboardPanelOutput, SavePanelInput, SavePanelOutput, SimpleGaugeWithValueAndLabelPanel } from '../../../auto/pieroxy-conkw';
import { TextFieldInput } from '../../forms/TextFieldInput';
import { SaveIcon } from '../../icons/SaveIcon';
import { RoundedPlusIcon } from '../../icons/RoundedPlusIcon';

export class EditSimpleGaugeWithValueAndLabelPanel extends AbstractPage<EditPanelAttrs> {
  private panel:SimpleGaugeWithValueAndLabelPanel;
  //private dashboardId:string;

  getPageTitle(): string {
    return "New panel";
  }

  oninit({attrs}:m.Vnode<EditPanelAttrs>) {
    Api.call<GetDashboardPanelInput, GetDashboardPanelOutput>({
      method:"GET",
      endpoint:"GetDashboardPanel",
      body:{
        dashboardId:attrs.dashboardId,
        panelId:attrs.panelId
      }
    }).then((output) => {
      this.panel = output.panel;
      //this.dashboardId = output.dashboardId;
    })
  }
  
  render({attrs}:m.Vnode<EditPanelAttrs>):m.Children {
    if (!this.panel) return m("");
    let id = attrs.dashboardId;
    return m(".page", [
      m(".title", [
        m("a.floatright", {title:"Save panel", onclick:()=>{
          Api.call<SavePanelInput, SavePanelOutput>({
            method:"POST",
            endpoint:"SavePanel",
            body:{
              dashboardId:id,
              panel:this.panel
            }
          })
        }}, m(SaveIcon)),
        m("a.floatright.rm10", {title:"New gauge", onclick:()=>{}}, m(RoundedPlusIcon)),

        m("a", {title:"Go back Home", href:Routing.getRouteAsHref(Endpoints.HOME)}, m(HomeIcon)),
        m(RightChevronIcon),
        m("a", {href:Routing.getRouteAsHref(Endpoints.DASHBOARD_EDITION, {id:id})}, GlobalData.getDashboardTitle(id)),
        m(RightChevronIcon),
        "Edit Panel"
      ]),
      m(".panel", {
        
      }, [
        m(".title", 
          m(TextFieldInput, {
            refHolder:this.panel,
            refProperty:"title",
            onenter() {},
          })
        ),
        m("br", {clear:"both"})
      ])
    ]);
  }

}

export interface EditPanelAttrs {
  dashboardId:string;
  panelId:string;
}
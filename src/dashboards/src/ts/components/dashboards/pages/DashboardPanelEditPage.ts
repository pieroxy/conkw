import m from 'mithril';
import { HomeIcon } from '../../atoms/icons/HomeIcon';
import { RightChevronIcon } from '../../atoms/icons/RightChevronIcon';
import { Endpoints } from '../../../utils/navigation/Endpoints';
import { Routing } from '../../../utils/navigation/Routing';
import { AbstractPage } from '../../pages/AbstractPage';
import { GlobalData } from '../../../utils/GlobalData';
import { DashboardPanel, GaugeWithHistoryElement, SimpleGaugeWithValueAndLabelElement, TopLevelPanelElement, TopLevelPanelElementEnum } from '../../../auto/pieroxy-conkw';
import { TextFieldInput } from '../../atoms/forms/TextFieldInput';
import { SaveIcon } from '../../atoms/icons/SaveIcon';
import { RoundedPlusIcon } from '../../atoms/icons/RoundedPlusIcon';
import { ApiEndpoints } from '../../../auto/ApiEndpoints';
import { Link } from '../../atoms/Link';
import { NewPanelItemComponent } from '../edition/newitems/NewPanelItemComponent';
import { ViewDashboardPanel } from '../../panels/ViewDashboardPanel';
import { EditSimpleGaugeWithValueAndLabelElementPanel } from '../edition/panelitems/EditSimpleGaugeWithValueAndLabelElementPanel';
import { EditGaugeWithHistoryLinePanel } from '../edition/panelitems/EditGaugeWithHistoryLinePanel';

export class DashboardPanelEditPage extends AbstractPage<EditPanelAttrs> {
  private panel:DashboardPanel;
  private dashboardId:string;
  selected?: TopLevelPanelElement;

  getPageTitle(): string {
    return "New panel";
  }

  oninit({attrs}:m.Vnode<EditPanelAttrs>) {
    this.dashboardId = attrs.dashboardId;
    this.reload(attrs.dashboardId, attrs.panelId);
  }

  reload(dashboardId:string, panelId:string) {
    ApiEndpoints.GetDashboardPanel.call({
      dashboardId:dashboardId,
      panelId:panelId
    }).then((output) => {
      this.panel = output.panel;
    });
  }
  
  render({attrs}:m.Vnode<EditPanelAttrs>):m.Children {
    if (!this.panel) return m("");
    let id = attrs.dashboardId;
    return m(".page", [
      m(".title", [
        m("a.floatright", {title:"Save panel", onclick:()=>{
          ApiEndpoints.SavePanel.call({
              dashboardId:id,
              panel:this.panel
            }
          )
        }}, m(SaveIcon)),
        m(Link, {tooltip:"Go back Home", target:Endpoints.HOME}, m(HomeIcon)),
        m(RightChevronIcon),
        m(Link, {target:Routing.getRoute(Endpoints.DASHBOARD_VIEW, {id:id})}, GlobalData.getDashboardTitle(id)),
        m(RightChevronIcon),
        this.panel ? this.panel.title : "Edit Panel"
      ]),
      m(".dualpanel.content", [
        m(".panel.editable", [
          m(".title", 
            m(Link, {
              className:"floatright rm10", 
              tooltip:"New gauge", 
              target:()=>this.selected = undefined
            }, m(RoundedPlusIcon)),
  
            m(TextFieldInput, {
              className:".panelTitle",
              refHolder:this.panel,
              refProperty:"title",
              onenter() {},
              spellcheck:true
            })
          ),
          m("br", {clear:"both"}),
          (this.panel.content||[]).map(pi => m(".editableBlock", [
            m("a.clickableLayer" + (this.selected === pi ? ".editing":""), {
              onclick:() => {
                this.selected = pi;
              }
            }),
            ViewDashboardPanel.getDashboardPanel(pi)
          ]))
        ]),
        m(".nextToPanel", this.getDashboardPanelEditableView(this.selected))
      ])
    ]);
  }

  public getDashboardPanelEditableView(element?:TopLevelPanelElement):m.Children {
    if (!this.panel) return null;
    if (!element) {
      return m(NewPanelItemComponent, {
        dashboardId:this.dashboardId, 
        panelId:this.panel.id,
        addNewItem:(e) => this.panel.content.push(e)
      });
    }
    switch (element.type) {
      case TopLevelPanelElementEnum.SIMPLE_GAUGE:
        return m(EditSimpleGaugeWithValueAndLabelElementPanel, {element:<SimpleGaugeWithValueAndLabelElement>element})
      case TopLevelPanelElementEnum.GAUGE_WITH_HISTORY:
        return m(EditGaugeWithHistoryLinePanel, {element:<GaugeWithHistoryElement>element})
      default:
        return m("", "Unknown element type " + element.type);
    }
  }

}

export interface EditPanelAttrs {
  dashboardId:string;
  panelId:string;
}




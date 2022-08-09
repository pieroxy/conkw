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
import { EditSimpleLabelPanel } from '../../dashboardEdition/EditSimpleLabelPanel';
import { EditSimpleSiLabelPanel } from '../../dashboardEdition/EditSimpleSiLabelPanel';
import { EditSimpleGaugePanel } from '../../dashboardEdition/EditSimpleGaugePanel';
import { ApiEndpoints } from '../../../auto/ApiEndpoints';
import { Link } from '../../atoms/Link';
import { NewPanelItemComponent } from '../../dashboardEdition/NewPanelItemComponent';
import { ViewDashboardPanel } from '../../panels/ViewDashboardPanel';

export class EditDashboardPanelPage extends AbstractPage<EditPanelAttrs> {
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
        refreshData:() => this.reload(this.dashboardId,this.panel.id)
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

export class EditGaugeWithHistoryLinePanel implements m.ClassComponent<EditGaugeWithHistoryLinePanelAttrs> {
  view({attrs}: m.Vnode<EditGaugeWithHistoryLinePanelAttrs, this>): void | m.Children {
    if (attrs.element) {
      return [
        m(".title", ["Edit ", m("i", attrs.element.label.value.value)]),
        m(".editionPanel", [
          m(".group", [
            m(".groupTitle", "The label"),
            m(EditSimpleLabelPanel, {element:attrs.element.label}),
          ]),
          m(".group", [
            m(".groupTitle", "The value"),
            m(EditSimpleSiLabelPanel, {element:attrs.element.value}),
          ]),
          m(".group", [
            m(".groupTitle", "The gauge"),
            m(EditSimpleGaugePanel, {element:attrs.element.gauge}),
          ]),
          m(".group", [
            m(".groupTitle", "Specific properties"),
              m(".inputWithLabel", [
                m(".label", "Gauge number of lines:"),
                m(TextFieldInput, {
                  onenter:()=>{},
                  refHolder:attrs.element.gauge,
                  refProperty:"nbLinesHeight",
                  spellcheck:false,
                  params: {
                    size:3
                  }
                })
              ])
            ]),
          ])
      ];
    }
    return m("");
  }

}

export class EditSimpleGaugeWithValueAndLabelElementPanel implements m.ClassComponent<EditSimpleGaugeWithValueAndLabelElementPanelAttrs> {
  view({attrs}: m.Vnode<EditSimpleGaugeWithValueAndLabelElementPanelAttrs, this>): void | m.Children {
    if (attrs.element) {
      return [
        m(".title", ["Edit ", m("i", attrs.element.label.value.value)]),
        m(".editionPanel", [
          m(".group", [
            m(".groupTitle", "The label"),
            m(EditSimpleLabelPanel, {element:attrs.element.label}),
          ]),
          m(".group", [
            m(".groupTitle", "The value"),
            m(EditSimpleSiLabelPanel, {element:attrs.element.value}),
          ]),
          m(".group", [
            m(".groupTitle", "The gauge"),
            m(EditSimpleGaugePanel, {element:attrs.element.gauge}),
          ])
        ]),
      ];
    }
    return m("");
  }

}

export interface EditSimpleGaugeWithValueAndLabelElementPanelAttrs {
  element?:SimpleGaugeWithValueAndLabelElement;
}

export interface EditGaugeWithHistoryLinePanelAttrs {
  element?:GaugeWithHistoryElement;
}

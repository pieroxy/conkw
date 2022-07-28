import m from 'mithril';
import { HomeIcon } from '../../icons/HomeIcon';
import { RightChevronIcon } from '../../icons/RightChevronIcon';
import { Endpoints } from '../../../utils/navigation/Endpoints';
import { Routing } from '../../../utils/navigation/Routing';
import { AbstractPage } from '../AbstractPage';
import { GlobalData } from '../../../utils/GlobalData';
import { Api } from '../../../utils/api/Api';
import { ExpressionClass, ExpressionValueType, GetDashboardPanelInput, GetDashboardPanelOutput, SavePanelInput, SavePanelOutput, SimpleGaugeWithValueAndLabelElement, SimpleGaugeWithValueAndLabelPanel } from '../../../auto/pieroxy-conkw';
import { TextFieldInput } from '../../forms/TextFieldInput';
import { SaveIcon } from '../../icons/SaveIcon';
import { RoundedPlusIcon } from '../../icons/RoundedPlusIcon';
import { SimpleGaugeWithValueAndLabelElementComponent } from '../../organisms/dashboards/SimpleGaugeWithValueAndLabelElementComponent';

export class EditSimpleGaugeWithValueAndLabelPanel extends AbstractPage<EditPanelAttrs> {
  private panel:SimpleGaugeWithValueAndLabelPanel;
  selected?: SimpleGaugeWithValueAndLabelElement;
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
      this.panel = <SimpleGaugeWithValueAndLabelPanel>output.panel;
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
        m("a.floatright.rm10", {title:"New gauge", onclick:()=>{
          if (!this.panel.content) this.panel.content = [];
          this.panel.content.push({
            gauge: {
              min: {
                clazz: ExpressionClass.LITERAL,
                type:ExpressionValueType.NUMERIC,
                value:"0"
              },
              max: {
                clazz: ExpressionClass.LITERAL,
                type:ExpressionValueType.NUMERIC,
                value:"100"
              },
              value: {
                clazz: ExpressionClass.LITERAL,
                type:ExpressionValueType.NUMERIC,
                directive:"",
                value:"75"
              }
            },
            label: {
              value: {
                clazz: ExpressionClass.LITERAL,
                type:ExpressionValueType.STRING,
                value:"The string"
              }
            },
            value: {
              value: {
                clazz: ExpressionClass.LITERAL,
                type:ExpressionValueType.NUMERIC,
                value:"10000000000"
              },
              unit:"S",
              thousand:1000
            }
          })
        }}, m(RoundedPlusIcon)),

        m("a", {title:"Go back Home", href:Routing.getRouteAsHref(Endpoints.HOME)}, m(HomeIcon)),
        m(RightChevronIcon),
        m("a", {href:Routing.getRouteAsHref(Endpoints.DASHBOARD_EDITION, {id:id})}, GlobalData.getDashboardTitle(id)),
        m(RightChevronIcon),
        "Edit Panel"
      ]),
      m(".dualpanel", [
        m(".panel.editable", {
          
        }, [
          m(".title", 
            m(TextFieldInput, {
              refHolder:this.panel,
              refProperty:"title",
              onenter() {},
            })
          ),
          m("br", {clear:"both"}),
          (this.panel.content||[]).map(pi => m(".editableBlock", [
            m("a.clickableLayer", {
              onclick:() => {
                this.selected = pi;
              }
            }),
            m(SimpleGaugeWithValueAndLabelElementComponent, {
              currentData:<any>{},
              parent:null,
              model:pi
            }),
          ]))
        ]),
        m(".nextToPanel", m(EditSimpleGaugeWithValueAndLabelElementPanel, {element:this.selected}))
      ])
    ]);
  }
}

export interface EditPanelAttrs {
  dashboardId:string;
  panelId:string;
}

export class EditSimpleGaugeWithValueAndLabelElementPanel implements m.ClassComponent<EditSimpleGaugeWithValueAndLabelElementPanelAttrs> {
  view({attrs}: m.Vnode<EditSimpleGaugeWithValueAndLabelElementPanelAttrs, this>): void | m.Children {
    if (attrs.element) {
      return m("", "Editing the thing here ", attrs.element?.label.value.value);
    }
    return m("");
  }

}

export interface EditSimpleGaugeWithValueAndLabelElementPanelAttrs {
  element?:SimpleGaugeWithValueAndLabelElement;
}
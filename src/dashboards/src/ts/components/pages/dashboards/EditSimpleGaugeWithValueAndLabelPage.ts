import m from 'mithril';
import { HomeIcon } from '../../icons/HomeIcon';
import { RightChevronIcon } from '../../icons/RightChevronIcon';
import { Endpoints } from '../../../utils/navigation/Endpoints';
import { Routing } from '../../../utils/navigation/Routing';
import { AbstractPage } from '../AbstractPage';
import { GlobalData } from '../../../utils/GlobalData';
import { ExpressionClass, ExpressionValueType, SimpleGaugeWithValueAndLabelElement, SimpleGaugeWithValueAndLabelPanel } from '../../../auto/pieroxy-conkw';
import { TextFieldInput } from '../../atoms/forms/TextFieldInput';
import { SaveIcon } from '../../icons/SaveIcon';
import { RoundedPlusIcon } from '../../icons/RoundedPlusIcon';
import { SimpleGaugeWithValueAndLabelElementComponent } from '../../organisms/dashboards/SimpleGaugeWithValueAndLabelElementComponent';
import { EditSimpleLabelPanel } from '../../panels/dashboardEdition/EditSimpleLabelPanel';
import { EditSimpleSiLabelPanel } from '../../panels/dashboardEdition/EditSimpleSiLabelPanel';
import { EditSimpleGaugePanel } from '../../panels/dashboardEdition/EditSimpleGaugePanel';
import { MetricsReader } from '../../../utils/api/MetricsReader';
import { ApiEndpoints } from '../../../auto/ApiEndpoints';
import { Link } from '../../atoms/Link';

export class EditSimpleGaugeWithValueAndLabelPage extends AbstractPage<EditPanelAttrs> {
  private panel:SimpleGaugeWithValueAndLabelPanel;
  selected?: SimpleGaugeWithValueAndLabelElement;
  //private dashboardId:string;

  getPageTitle(): string {
    return "New panel";
  }

  oninit({attrs}:m.Vnode<EditPanelAttrs>) {
    ApiEndpoints.GetDashboardPanel.call({
        dashboardId:attrs.dashboardId,
        panelId:attrs.panelId
      }
    ).then((output) => {
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
          ApiEndpoints.SavePanel.call({
              dashboardId:id,
              panel:this.panel
            }
          )
        }}, m(SaveIcon)),
        m("a.floatright.rm10", {title:"New gauge", onclick:()=>{
          if (!this.panel.content) this.panel.content = [];
          this.panel.content.push({
            gauge: {
              min: {
                namespace:"",
                clazz: ExpressionClass.LITERAL,
                type:ExpressionValueType.NUMERIC,
                value:"0"
              },
              max: {
                namespace:"",
                clazz: ExpressionClass.LITERAL,
                type:ExpressionValueType.NUMERIC,
                value:"100"
              },
              value: {
                namespace:"",
                clazz: ExpressionClass.LITERAL,
                type:ExpressionValueType.NUMERIC,
                directive:"",
                value:"75"
              }
            },
            label: {
              value: {
                namespace:"",
                clazz: ExpressionClass.LITERAL,
                type:ExpressionValueType.STRING,
                value:"The string"
              }
            },
            value: {
              value: {
                namespace:"",
                clazz: ExpressionClass.LITERAL,
                type:ExpressionValueType.NUMERIC,
                value:"10000000000"
              },
              unit:"S",
              thousand:1000
            }
          })
        }}, m(RoundedPlusIcon)),

        m(Link, {tooltip:"Go back Home", target:Endpoints.HOME}, m(HomeIcon)),
        m(RightChevronIcon),
        m(Link, {target:Routing.getRoute(Endpoints.DASHBOARD_EDITION, {id:id})}, GlobalData.getDashboardTitle(id)),
        m(RightChevronIcon),
        "Edit Panel"
      ]),
      m(".dualpanel.content", [
        m(".panel.editable", [
          m(".title", 
            m(TextFieldInput, {
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
            m(SimpleGaugeWithValueAndLabelElementComponent, {
              currentData:MetricsReader.getLastResponse(),
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
      return m(".editionPanel", [
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
      ]);
    }
    return m("");
  }

}

export interface EditSimpleGaugeWithValueAndLabelElementPanelAttrs {
  element?:SimpleGaugeWithValueAndLabelElement;
}
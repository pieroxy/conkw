import m from 'mithril';
import { HomeIcon } from '../../atoms/icons/HomeIcon';
import { RightChevronIcon } from '../../atoms/icons/RightChevronIcon';
import { Endpoints } from '../../../utils/navigation/Endpoints';
import { Routing } from '../../../utils/navigation/Routing';
import { AbstractPage } from '../AbstractPage';
import { GlobalData } from '../../../utils/GlobalData';
import { ApiEndpoints } from '../../../auto/ApiEndpoints';
import { Link } from '../../atoms/Link';
import { SimpleGaugeWithValueAndLabelElementComponent } from '../../organisms/dashboards/SimpleGaugeWithValueAndLabelElementComponent';
import { ExpressionClass, ExpressionValueType, SimpleGaugeWithValueAndLabelElement, TopLevelPanelElementEnum } from '../../../auto/pieroxy-conkw';

export class NewPanelItemPage extends AbstractPage<NewPanelPageAttrs> {

  getPageTitle(): string {
    return "New panel";
  }
  
  render({attrs}:m.Vnode<NewPanelPageAttrs>):m.Children {
    let id = attrs.dashboardId;
    return m(".page", [
      m(".title", [
        m(Link, {tooltip:"Go back Home", target:Endpoints.HOME}, m(HomeIcon)),
        m(RightChevronIcon),
        m(Link, {target:Routing.getRoute(Endpoints.DASHBOARD_EDITION, {id:id})}, GlobalData.getDashboardTitle(id)),
        m(RightChevronIcon),
        "Panel"
      ]),
      m(".panel.clickable", {
        onclick:() => {
          ApiEndpoints.NewPanelItem.call({
              dashboardId:attrs.dashboardId,
              panelId:attrs.panelId,
              type:TopLevelPanelElementEnum.SIMPLE_GAUGE
            }
          ).then((output) => {
            Routing.goToScreen(Endpoints.DASHBOARD_PANEL_EDIT, {dashboardId:output.dashboardId, panelId:output.panelId})
          });
        }
      }, [
        m(".title", "Simple gauge with value and label"),
        m(SimpleGaugeWithValueAndLabelElementComponent, 
        {
          currentData:{
            metrics: {},
            timestamp: 0,
            instanceName: "string",
            responseJitter: 0,
            needsAuthentication: false,
            errors: [],
            numCount: 0,
            strCount: 0,
          },
          model: <SimpleGaugeWithValueAndLabelElement>{ 
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
                value:"CPU Usage"
              }
            },
            value: {
              value: {
                namespace:"",
                clazz: ExpressionClass.LITERAL,
                type:ExpressionValueType.NUMERIC,
                value:"75"
              },
              unit:"%",
              thousand:1000
            }
          }
        }),
        m("br", {clear:"both"})
      ])
    ]);
  }
}

export interface NewPanelPageAttrs {
  dashboardId:string;
  panelId:string;
}
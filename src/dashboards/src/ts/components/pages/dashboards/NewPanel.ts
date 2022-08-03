import m from 'mithril';
import { HomeIcon } from '../../icons/HomeIcon';
import { RightChevronIcon } from '../../icons/RightChevronIcon';
import { Endpoints } from '../../../utils/navigation/Endpoints';
import { Routing } from '../../../utils/navigation/Routing';
import { AbstractPage } from '../AbstractPage';
import { GlobalData } from '../../../utils/GlobalData';
import { DashboardPanelType } from '../../../auto/pieroxy-conkw';
import { ApiEndpoints } from '../../../auto/ApiEndpoints';

export class NewPanelPage extends AbstractPage<NewPanelPageAttrs> {

  getPageTitle(): string {
    return "New panel";
  }
  
  render({attrs}:m.Vnode<NewPanelPageAttrs>):m.Children {
    let id = attrs.dashboardId;
    return m(".page", [
      m(".title", [
        m("a", {title:"Go back Home", onclick:()=>Routing.goToScreen(Endpoints.HOME)}, m(HomeIcon)),
        m(RightChevronIcon),
        m("a", {onclick:()=>Routing.goToScreen(Endpoints.DASHBOARD_EDITION, {id:id})}, GlobalData.getDashboardTitle(id)),
        m(RightChevronIcon),
        "Panel"
      ]),
      m(".panel.clickable", {
        onclick:() => {
          ApiEndpoints.NewPanel.call({
              dashboardId:attrs.dashboardId,
              type:DashboardPanelType.SIMPLE_GAUGE_WITH_VALUE_AND_LABEL
            }
          ).then((output) => {
            Routing.goToScreen(Endpoints.GAUGE_SIMPLE_VALUE_LABEL_EDIT, {dashboardId:attrs.dashboardId, panelId:output.panelId})
          });
        }
      }, [
        m(".title", "Simple gauge with value and label"),
        m(".line", [
          m(".placeholderText", "Some thing"),
          m(".placeholderGauge", {style:{borderLeftWidth:"5px"}}),
          m(".placeholderValue", "5.00"),
        ]),
        m(".line", [
          m(".placeholderText", "some other thing"),
          m(".placeholderGauge", {style:{borderLeftWidth:"100px"}}),
          m(".placeholderValue", "100."),
        ]),
        m(".line", [
          m(".placeholderText", "The third one"),
          m(".placeholderGauge", {style:{borderLeftWidth:"85px"}}),
          m(".placeholderValue", "85.0"),
        ]),
        m(".line", [
          m(".placeholderText", "All is lost"),
          m(".placeholderGauge", {style:{borderLeftWidth:"135px"}}),
          m(".placeholderValue", "135."),
        ]),
        m(".line", [
          m(".placeholderText", "Or not"),
          m(".placeholderGauge", {style:{borderLeftWidth:"52px"}}),
          m(".placeholderValue", "52.0"),
        ]),
        m("br", {clear:"both"})
      ])
    ]);
  }
}

export interface NewPanelPageAttrs {
  dashboardId:string;
}
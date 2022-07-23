import m from 'mithril';
import { HomeIcon } from '../../icons/HomeIcon';
import { RightChevronIcon } from '../../icons/RightChevronIcon';
import { Endpoints } from '../../../utils/navigation/Endpoints';
import { Routing } from '../../../utils/navigation/Routing';
import { AbstractPage } from '../AbstractPage';
import { GlobalData } from '../../../utils/GlobalData';

export class NewPanelPage extends AbstractPage<NewPanelPageAttrs> {

  getPageTitle(): string {
    return "New panel";
  }
  
  render({attrs}:m.Vnode<NewPanelPageAttrs>):m.Children {
    let id = attrs.dashboardId;
    return m(".page", [
      m(".title", [
        m("a", {title:"Go back Home", href:Routing.getRouteAsHref(Endpoints.HOME)}, m(HomeIcon)),
        m(RightChevronIcon),
        m("a", {href:Routing.getRouteAsHref(Endpoints.DASHBOARD_EDITION, {id:id})}, GlobalData.getDashboardTitle(id)),
        m(RightChevronIcon),
        "Panel"
      ]),
    ]);
  }
}

export interface NewPanelPageAttrs {
  dashboardId:string;
}
import m from 'mithril';

import { Children } from "mithril";
import { Api } from '../../utils/api/Api';
import { Auth, AuthenticationStatus } from '../../utils/Auth';
import { Endpoints } from '../../utils/navigation/Endpoints';
import { Routing } from '../../utils/navigation/Routing';
import { AlertsIcon } from '../icons/AlertsIcon';
import { DashboardIcon } from '../icons/DashboardIcon';
import { ProfileIcon } from '../icons/ProfileIcon';

export class MainTopBar implements m.ClassComponent<any> {
  
  view(): void | Children {
    return m(".topbar", [
      m("img.logo", {
        src:"/images/medium_logo_transparent_50prc.png"
      }),
      Auth.getAuthenticationStatus() != AuthenticationStatus.LOGGED_IN ? null :
      m(".pages", [
        m(TopBarPageIcon, {
          route:Routing.getRoute(Endpoints.HOME),
          icon:DashboardIcon,
          name:"Dashboards"
        }),
        m(TopBarPageIcon, {
          route:Routing.getRoute(Endpoints.ALERTS),
          icon:AlertsIcon,
          name:"Alerts"
        }),
        m(TopBarPageIcon, {
          route:Routing.getRoute(Endpoints.PROFILE),
          icon:ProfileIcon,
          name:"You"
        }),
      ]),
      Api.isWaiting() ? m(".loader", "Waiting") : null,
    ])
  }
}

class TopBarPageIcon implements m.ClassComponent<TopBarPageIconAttrs> {
  view({attrs}:m.Vnode<TopBarPageIconAttrs>) {
    return m("a", {
      className:attrs.route.startsWith(m.route.get())?"selected":"",
      onclick:() => { m.route.set(attrs.route); return false; }
    }, m(attrs.icon));

  }
}

interface TopBarPageIconAttrs {
  name:String;
  icon:m.ComponentTypes<any,any>;
  route:string;
}
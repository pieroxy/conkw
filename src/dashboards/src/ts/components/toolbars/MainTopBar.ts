import m from 'mithril';

import { Children } from "mithril";
import { UserRole } from '../../auto/pieroxy-conkw';
import { Api } from '../../utils/api/Api';
import { Auth, AuthenticationStatus } from '../../utils/Auth';
import { Endpoints } from '../../utils/navigation/Endpoints';
import { Routing } from '../../utils/navigation/Routing';
import { Link } from '../atoms/Link';
import { AlertsIcon } from '../icons/AlertsIcon';
import { DashboardIcon } from '../icons/DashboardIcon';
import { LogoCIcon } from '../icons/LogoC';
import { ProfileIcon } from '../icons/ProfileIcon';
import { SettingsIcon } from '../icons/SettingsIcon';

export class MainTopBar implements m.ClassComponent<any> {
  
  view(): void | Children {
    return m(".topbar", [
      m("span.logo", m(LogoCIcon)),
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
        Auth.getUser()?.role !== UserRole.ADMIN ? null :
        m(TopBarPageIcon, {
          route:Routing.getRoute(Endpoints.SETTINGS),
          icon:SettingsIcon,
          name:"Server settings"
        }),
      ]),
      Api.isWaiting() ? m(".loader", "Waiting") : null,
    ])
  }
}

class TopBarPageIcon implements m.ClassComponent<TopBarPageIconAttrs> {
  view({attrs}:m.Vnode<TopBarPageIconAttrs>) {
    return m(Link, {
      className:m.route.get().startsWith(attrs.route)?"selected":"",
      target:attrs.route,
      tooltip:attrs.name
    }, m(attrs.icon));

  }
}

interface TopBarPageIconAttrs {
  name:string;
  icon:m.ComponentTypes<any,any>;
  route:string;
}
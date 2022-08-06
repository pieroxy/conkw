import m from 'mithril';

import { Children } from "mithril";
import { UserRole } from '../../auto/pieroxy-conkw';
import { Api } from '../../utils/api/Api';
import { Auth, AuthenticationStatus } from '../../utils/Auth';
import { Endpoints } from '../../utils/navigation/Endpoints';
import { Routing } from '../../utils/navigation/Routing';
import { Link } from '../atoms/Link';
import { AlertsIcon } from '../atoms/icons/AlertsIcon';
import { DashboardIcon } from '../atoms/icons/DashboardIcon';
import { LogoCIcon } from '../atoms/icons/LogoC';
import { ProfileIcon } from '../atoms/icons/ProfileIcon';
import { SettingsIcon } from '../atoms/icons/SettingsIcon';
import { RefreshIcon } from '../atoms/icons/RefreshIcon';

export class MainTopBar implements m.ClassComponent<MainTopBarAttrs> {
  
  view({attrs}:m.Vnode<MainTopBarAttrs>): void | Children {
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
      Api.isWaiting() ? m(".loader", "Waiting") : 
      (attrs.refreshData ? m(Link, {target:attrs.refreshData}, m(RefreshIcon)) : null),
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

interface MainTopBarAttrs {
  refreshData?:() => void;
}
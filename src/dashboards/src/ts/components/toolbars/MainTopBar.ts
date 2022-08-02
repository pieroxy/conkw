import m from 'mithril';

import { Children } from "mithril";
import { Api } from '../../utils/api/Api';
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
      m(".pages", [
        m(m.route.Link, {
          href:Routing.getRoute(Endpoints.HOME)
        }, m(DashboardIcon)),
        m("span", " "),
        m(m.route.Link, {
          href:Routing.getRoute(Endpoints.ALERTS)
        }, m(AlertsIcon)),
        m("span", " "),
        m(m.route.Link, {
          href:Routing.getRoute(Endpoints.PROFILE)
        }, m(ProfileIcon)),
      ]),
      Api.isWaiting() ? m(".loader", "Waiting") : null,
    ])
  }
}
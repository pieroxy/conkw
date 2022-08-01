import m from 'mithril';
import { AppVersion } from '../../../auto/version';
import { MithrilIcon } from '../../icons/MithrilIcon';

export class LoginFooter implements m.ClassComponent {
  view() {
    return m(".footer", [
      m("span", m("a", {href:"/doc/README.html", title:"Low level doc"}, "Documentation")),
      m("span", m("a", {href:"https://github.com/pieroxy/conkw", title:"The code, the community"}, "Github")),
      m("span", {title:AppVersion.GIT_REV + " - " + AppVersion.GIT_DEPTH}, "Ver: ", AppVersion.MVN_VER),
      m("span", m("a", {href:"https://mithril.js.org/", title:"Do not settle for anything but the best"}, m("span", [m(MithrilIcon), " enabled by Mitril"])))
    ])
  }
}
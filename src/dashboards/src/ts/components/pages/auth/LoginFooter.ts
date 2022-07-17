import m from 'mithril';
import { AppVersion } from '../../../auto/version';

export class LoginFooter implements m.ClassComponent {
  view() {
    return m(".footer", [
      m("span", m("a", {href:"/doc/README.html"}, "Documentation")),
      m("span", m("a", {href:"https://github.com/pieroxy/conkw"}, "Github")),
      m("span", "Ver: ", AppVersion.MVN_VER, " (", AppVersion.GIT_REV, ", " + AppVersion.GIT_DEPTH + ")")
    ])

  }
}
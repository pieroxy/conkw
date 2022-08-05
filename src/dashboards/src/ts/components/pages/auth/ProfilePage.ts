import m from 'mithril';
import { AbstractPage } from '../AbstractPage';
import { Button } from '../../atoms/forms/Button';
import { Auth } from '../../../utils/Auth';
import { Routing } from '../../../utils/navigation/Routing';
import { Endpoints } from '../../../utils/navigation/Endpoints';
import { ApiEndpoints } from '../../../auto/ApiEndpoints';
import { GetAllUserSessionsOutput } from '../../../auto/pieroxy-conkw';
import { Link } from '../../atoms/Link';
import { DeleteIcon } from '../../atoms/icons/DeleteIcon';
import { DateUtils } from '../../../utils/DateUtils';
import { UserAgentUtils } from '../../../utils/UserAgentUtils';

export class ProfilePage extends AbstractPage<any> {
  private sessions:GetAllUserSessionsOutput;

  getPageTitle(): string {
    return "You";
  }

  load() {
    ApiEndpoints.GetAllUserSessions.call({}).then((sessions) => {this.sessions = sessions});
  }

  oninit() {
    this.load();
  }

  render():m.Children {
    return m(".page", [
      m(".title", "Informations"),
      m(".content", [
        m("", "Soon informations about you here"),
        m("hr"),
        m(".title", "Password"),
        m(Button, {
          action:() => {
            let id = Auth.getUser()?.id;
            if (!id) return;
            Routing.goToScreen(Endpoints.PASSWORD_CHANGE, {id:id});
          },
          secondary:true
        }, "Change password"),
        m("hr"),
        m(".title", "Sessions"),
        !this.sessions ? m("", "Loading...") : 
        m("table", [
          m("tr", [m("th", "Last seen"), m("th", "Created"), m("th", "From IP"), m("th", "From Browser"), m("th")]),
          this.sessions.sessions.map(s => m("tr", [
            m("td", s.token === Auth.getAuthToken() ? "This session" : DateUtils.formatRoughDate(""+s.lastUsed)),
            m("td", DateUtils.formatRoughDate(""+s.creation)),
            m("td", s.ip),
            m("td", {title:s.userAgent}, UserAgentUtils.getDescription(s.userAgent)),
            m("td", m(Link, {target:()=>{ApiEndpoints.RemoveSession.call({token:s.token}).then(()=>{this.load()})}, }, m(DeleteIcon))),
          ]))
        ])
      ])
    ])
  }
}


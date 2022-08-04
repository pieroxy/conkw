import m from 'mithril';
import { AbstractPage } from '../AbstractPage';
import { Button } from '../../atoms/forms/Button';
import { Auth } from '../../../utils/Auth';
import { Routing } from '../../../utils/navigation/Routing';
import { Endpoints } from '../../../utils/navigation/Endpoints';

export class ProfilePage extends AbstractPage<any> {

  getPageTitle(): string {
    return "You";
  }

  render():m.Children {
    return m(".page", [
      m(".title", "Informations"),
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
      m(Button, {
        action:Auth.clearAuthToken,
        secondary:true
      }, "Logout")
    ])
  }
}


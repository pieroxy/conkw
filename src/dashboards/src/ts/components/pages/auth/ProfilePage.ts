import m from 'mithril';
import { AbstractPage } from '../AbstractPage';
import { Button } from '../../forms/Button';
import { Auth } from '../../../utils/Auth';

export class ProfilePage extends AbstractPage<any> {

  getPageTitle(): string {
    return "You";
  }

  render():m.Children {
    return m(".page", m(Button, {
      action:Auth.clearAuthToken
    }, "Logout"))
  }
}


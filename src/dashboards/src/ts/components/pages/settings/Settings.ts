import m from 'mithril';
import { Endpoints } from '../../../utils/navigation/Endpoints';
import { AbstractPage } from '../AbstractPage';
import { Link } from '../../atoms/Link';

export class SettingsPage extends AbstractPage<any> {

  getPageTitle(): string {
    return "Server settings";
  }
  
  render():m.Children {
    return m(".page", [
      m(".title", "Server settings"),
      m("hr"),
      m(".subtitle", "Users management"),
      m(Link, {target:Endpoints.ALERTS})
    ]);
  }
}

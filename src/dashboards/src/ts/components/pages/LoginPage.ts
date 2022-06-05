import m = require('mithril');
import { AbstractPage } from './AbstractPage';

export class LoginPage extends AbstractPage<any> {
  getPageTitle(): string {
    return "ConkW - Login";
  }
  render():m.Children {
    // TODO: Do something here
    return m("", "Please log in");
  }
}


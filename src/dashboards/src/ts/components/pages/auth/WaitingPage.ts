import m from 'mithril';
import { AbstractPage } from '../AbstractPage';
import { LoginFooter } from './LoginFooter';

export class WaitingPage extends AbstractPage<any> {
  getPageTitle(): string {
    return "ConkW - Waiting";
  }
  render():m.Children {
    return m(".loginpage", [
      m("", "Wait"),
      m(LoginFooter)
    ]);
  }
}

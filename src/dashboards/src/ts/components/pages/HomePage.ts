import m from 'mithril';
import { AbstractPage } from './AbstractPage';

export class HomePage extends AbstractPage<any> {
  getPageTitle(): string {
    // TODO: Do something here
    return "ConkW - Welcome!";
  }
  render(_vnode:m.Vnode<any, any>):m.Children {
    return m("", "ConkW - Welcome!");
  }
}


import m from 'mithril';
import { DashboardExplorer } from '../panels/DashboardExplorer';
import { AbstractPage } from './AbstractPage';

export class HomePage extends AbstractPage<any> {
  getPageTitle(): string {
    return "ConkW - Welcome!";
  }
  render(_vnode:m.Vnode<any, any>):m.Children {
    return m(".page", m(DashboardExplorer));
  }
}


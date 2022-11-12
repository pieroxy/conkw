import m from 'mithril';
import { AbstractPage } from '../AbstractPage';

export class ExtractorsListPage extends AbstractPage<any> {
  getPageTitle(): string {
    return "Not yet";
  }

  render():m.Children {
    return m(".page", "Nothing to see here (yet), please move along.")
  }
}


import m from 'mithril';
import { Endpoints } from '../../../utils/navigation/Endpoints';
import { AbstractPage } from '../AbstractPage';
import { FiltersIcon } from '../../atoms/icons/FiltersIcon';
import { Button } from '../../atoms/forms/Button';
import { Routing } from '../../../utils/navigation/Routing';

export class SettingsPage extends AbstractPage<any> {

  getPageTitle(): string {
    return "Server settings";
  }
  
  render():m.Children {
    return m(".page", [
      m(".title", "Server settings"),
      m("hr"),
      m(Button, {action:()=>Routing.goToScreen(Endpoints.EXTRACTORS_LIST)}, m(FiltersIcon), "Metrics and extractions management"),
      m("hr"),
      m(".subtitle", "Users management"),
    ]);
  }
}

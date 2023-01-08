import m from 'mithril';
import { Endpoints } from '../../../utils/navigation/Endpoints';
import { AbstractPage } from '../AbstractPage';
import { FiltersIcon } from '../../atoms/icons/FiltersIcon';
import { Button } from '../../atoms/forms/Button';
import { ApiEndpoints } from '../../../auto/ApiEndpoints';
import { Notification, Notifications, NotificationsClass, NotificationsType } from '../../../utils/Notifications';
import { Link } from '../../atoms/Link';

export class SettingsPage extends AbstractPage<any> {

  getPageTitle(): string {
    return "Server settings";
  }
  
  render():m.Children {
    return m(".page", [
      m(".title", "Server settings"),
      m("hr"),
      m("ul", [
        m("li", m(Link, {target:Endpoints.EXTRACTORS_LIST}, "Metrics and extractions management")),
        m("li", m(Button, {action:()=>{
          ApiEndpoints.ReloadAllGrabbersFromFile.call({})
          .then(() => Notifications.addNotification(
            new Notification(NotificationsClass.LOGIN, NotificationsType.INFO, "The configuration is being reloaded.", 5)
            ))}}, m(FiltersIcon), "Reload config file")
        )
      ]),
      m("hr"),
      m(".subtitle", "Users management"),
    ]);
  }
}

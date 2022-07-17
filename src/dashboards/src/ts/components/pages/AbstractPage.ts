import m from 'mithril';
import { Auth, AuthenticationStatus } from '../../utils/Auth';
import { Notifications } from '../../utils/Notifications';
import { NotificationComponent } from '../organisms/NotificationComponent';
import { MainTopBar } from '../toolbars/MainTopBar';


export abstract class AbstractPage<A> implements m.ClassComponent<A> {
  public view(vnode:m.Vnode<A, any>):void|m.Children {
    window.document.title = this.getPageTitle();
    return [
      renderNotification(),
      Auth.getAuthenticationStatus()===AuthenticationStatus.LOGGED_IN ?m(MainTopBar):null,
      this.render(vnode),
    ];
  }
  abstract getPageTitle():string;
  abstract render(vnode:m.Vnode<A>):m.Children;
}

function renderNotification(): m.Children {
  let n = Notifications.getTopNotification();
  if (n) {
    return m(NotificationComponent, {notification:n})
  } else {
    return null;
  }
}

import m from 'mithril';
import { Notifications } from '../../utils/Notifications';
import { NotificationComponent } from '../organisms/NotificationComponent';


export abstract class AbstractPage<A> implements m.ClassComponent<A> {
  public view(vnode:m.Vnode<A, any>):void|m.Children {
    window.document.title = this.getPageTitle();
    return [
      renderNotification(),
      this.render(vnode),

    ];
  }
  abstract getPageTitle():string;
  abstract render(vnode:m.Vnode<A, any>):m.Children;
}

function renderNotification(): m.Children {
  let n = Notifications.getTopNotification();
  if (n) {
    return m(NotificationComponent, {notification:n})
  } else {
    return null;
  }
}

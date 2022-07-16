import m from 'mithril';
import { Notifications, NotificationsType } from '../alwayson/Notifications';


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
    let icon = "";
    switch (n.type) {
      case NotificationsType.INFO:
        icon = "ℹ️";
        break;
      case NotificationsType.WARNING:
        icon = "⚠️";
        break;
      case NotificationsType.ERROR:
        icon = "⛔";
        break;
        }

    return m(".notificationO", m(".notification." + n.type, [
      icon," ",
      n.content,
      m(".closebtn", {
        onclick: () => Notifications.dismiss()
      })
    ]))
  } else {
    return null;
  }
}

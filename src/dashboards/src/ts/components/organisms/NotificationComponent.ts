import m from 'mithril';

import { Children } from "mithril";
import { Notification, Notifications, NotificationsType } from '../../utils/Notifications';

export class NotificationComponent implements m.ClassComponent<NotificationComponentAttrs> {
  private timeout?:number = undefined;

  oninit({attrs}:m.Vnode<NotificationComponentAttrs>) {
    if (attrs.notification.timeout>0) {
      this.timeout = window.setTimeout(() => {
        this.timeout = undefined;
        Notifications.dismiss(attrs.notification);
        m.redraw();
      }, attrs.notification.timeout*1000);
    }
  }

  onbeforeremove(vnode:m.VnodeDOM<NotificationComponentAttrs>) {
    if (this.timeout !== undefined) {
      window.clearTimeout(this.timeout);
      this.timeout = undefined;
    }
    vnode.dom.classList.add("closed")
    return new Promise(function(resolve) {
        vnode.dom.addEventListener("animationend", resolve)
    })
  }
  
  view({attrs}: m.Vnode<NotificationComponentAttrs>): void | Children {
    let n = attrs.notification;
    let icon = "";
    switch (n.type) {
      case NotificationsType.SUCCESS:
        icon = "✅";
        break;
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
      m(".typeicon", icon),
      m(".closebtn", {
        onclick: () => Notifications.dismiss()
      }),
      n.content,
    ]))
  }
}

export interface NotificationComponentAttrs {
  notification:Notification;
}
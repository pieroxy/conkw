import m from 'mithril';

import { Children } from "mithril";
import { Notification, Notifications, NotificationsType } from '../../utils/Notifications';
import { StatusErrorIcon } from '../atoms/icons/StatusErrorIcon';
import { StatusInfoIcon } from '../atoms/icons/StatusInfoIcon';
import { StatusOkIcon } from '../atoms/icons/StatusOkIcon';
import { StatusWarningIcon } from '../atoms/icons/StatusWarningIcon';

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

  onbeforeremove({dom}:m.VnodeDOM<NotificationComponentAttrs>) {
    if (this.timeout !== undefined) {
      window.clearTimeout(this.timeout);
      this.timeout = undefined;
    }
    dom.classList.add("closed")
    return new Promise(function(resolve) {
        dom.addEventListener("animationend", resolve)
    })
  }
  
  view({attrs}: m.Vnode<NotificationComponentAttrs>): void | Children {
    let n = attrs.notification;
    let icon:m.Children = "";
    switch (n.type) {
      case NotificationsType.SUCCESS:
        icon = m(StatusOkIcon);
        break;
      case NotificationsType.INFO:
        icon = m(StatusInfoIcon);
        break;
      case NotificationsType.WARNING:
        icon = m(StatusWarningIcon);
        break;
      case NotificationsType.ERROR:
        icon = m(StatusErrorIcon);
        break;
    }
    let ad = n.alreadyDisplayed;
    n.alreadyDisplayed = true; 

    return m(".notificationO" + (ad?"":".appear"), m(".notification." + n.type, [
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
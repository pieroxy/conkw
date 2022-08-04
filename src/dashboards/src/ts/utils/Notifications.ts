import m from 'mithril';

export class Notifications {
  private static content:Notification[] = [];
  private static current?:Notification;

  public static addNotification(n:Notification):void {
    if (this.current) {
      this.content.push(n);
      this.dismiss();
    } else 
      this.current = n;
  }
  public static getTopNotification():null|Notification {
    return this.current||null;
  }

  public static dismiss(notif?:Notification):void {
    if (notif) {
      if (notif !== Notifications.current) return;
    }
    Notifications.current = undefined;
    setTimeout(() => {
      Notifications.current = Notifications.content.pop();
      if (Notifications.current) m.redraw();
    }, 500);
  }
}

export class Notification {
  private _content: m.Children;
  public get content(): m.Children {
    return this._content;
  }
  private _type: NotificationsType;
  public get type(): NotificationsType {
    return this._type;
  }
  private _clazz: NotificationsClass;
  public get clazz(): NotificationsClass {
    return this._clazz;
  }
  private _timeout: number;
  public get timeout(): number {
    return this._timeout;
  }

  constructor(c: NotificationsClass, type:NotificationsType, content: m.Children, timeout:number) {
    this._type = type;
    this._content = content;
    this._clazz = c;
    this._timeout = timeout;
  }
}

export enum NotificationsType {
  ERROR,
  WARNING,
  INFO,
  SUCCESS
}

export enum NotificationsClass {
  LOGIN,
}
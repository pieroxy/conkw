import m from 'mithril';

export class Notifications {
  private static content:Notification[] = [];

  public static addNotification(n:Notification):void {
    this.content.push(n);
  }
  public static getTopNotification():null|Notification {
    return this.content.length == 0 ? null : Notifications.content[0];
  }

  public static dismiss(notif?:Notification):void {
    if (Notifications.content.length == 0) return;
    if (notif) {
      if (notif !== Notifications.content[0]) return;
    }
    Notifications.content.pop();
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
  INFO
}

export enum NotificationsClass {
  LOGIN,
}
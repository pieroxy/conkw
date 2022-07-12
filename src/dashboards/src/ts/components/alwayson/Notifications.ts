import m from 'mithril';

export class Notifications {
  private static content:Notification[] = [];

  public static addNotification(n:Notification):void {
    this.content.push(n);
  }
  public static getTopNotification():null|Notification {
    return this.content.length == 0 ? null : this.content[0];
  }

  public static dismiss():void {
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

  constructor(c: NotificationsClass, type:NotificationsType, content: m.Children) {
    this._type = type;
    this._content = content;
    this._clazz = c;
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
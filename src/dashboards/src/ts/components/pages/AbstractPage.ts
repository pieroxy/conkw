import m from 'mithril';
import { Dialogs } from '../../utils/Dialogs';
import { Notifications } from '../../utils/Notifications';
import { NotificationComponent } from '../organisms/NotificationComponent';
import { MainTopBar } from '../toolbars/MainTopBar';


export abstract class AbstractPage<A> implements m.ClassComponent<A> {
  public view(vnode:m.Vnode<A, any>):void|m.Children {
    window.document.title = this.getPageTitle();
    return [
      this.renderNotification(),
      this.renderDialog(),
      m(MainTopBar),
      this.render(vnode),
    ];
  }
  abstract getPageTitle():string;
  abstract render(vnode:m.Vnode<A>):m.Children;

  private renderNotification(): m.Children {
    let n = Notifications.getTopNotification();
    if (n) {
      return m(NotificationComponent, {notification:n})
    } else {
      return null;
    }
  }
  
  private renderDialog(): m.Children {
    let n = Dialogs.getCurrent();
    if (n) {
      return m(".dialogveil", n.render());
    } else {
      return null;
    }
  }
  
}


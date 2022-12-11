import m from 'mithril';
import { Dialogs } from '../../utils/Dialogs';
import { Notifications } from '../../utils/Notifications';
import { NotificationComponent } from '../organisms/NotificationComponent';
import { MainTopBar } from '../toolbars/MainTopBar';


export abstract class AbstractPage<A> implements m.ClassComponent<A> {
  private static headerHeight:number=-1.01;
  
  public view(vnode:m.Vnode<A, any>):void|m.Children {
    try {
      window.document.title = this.getPageTitle();
      this.computeHeaderHeight();
      return [
        this.renderJsAccessibleColors(),
        this.renderNotification(),
        this.renderDialog(),
        m(MainTopBar, {refreshData:this.refreshData}),
        this.render(vnode),
      ];
    } catch (e) {
      console.log(e);
      return m("", "Something bad happened. More details can be found in the console.")
    }
  }

  renderJsAccessibleColors(): m.Children {
    return m(".colorSetForJs", [
      m("#gaugeDefaultColor"),
      m("#gaugeErrorColor"),
      m("#gaugeErrorThumb"),
      m("#gaugeDefaultBackground"),
    ])
  }

  computeHeaderHeight() {
    let topbar = document.getElementsByClassName("topbar")[0];
    if (topbar) {
      let title = document.querySelector(".page .title");
      if (title) {
        let h = window.document.body.clientHeight - (topbar.clientHeight + title.clientHeight + 10);
        if (h!=AbstractPage.headerHeight) {
          let lastHeight = AbstractPage.headerHeight;
          AbstractPage.headerHeight = h;
          if (lastHeight == -1.01) {
            document.styleSheets[0].insertRule(".content { height:" + h + "px; }");
          } else {
            let csss = document.styleSheets[0].cssRules;
            for (let i=0 ; i<csss.length ; i++) {
              let rule = csss[i];
              if (rule.cssText.replace(/ /g, "").startsWith(".content{height:")) {
                (<any>rule).style.height = h + "px";
                return;
              }
            }
          }
        }
      }
    }
  }

  abstract getPageTitle():string;
  abstract render(vnode:m.Vnode<A>):m.Children;
  refreshData?:()=>void = undefined;

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


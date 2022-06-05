import m = require('mithril');


export abstract class AbstractPage<A> implements m.Component<A, any> {
  public view(vnode:m.Vnode<A, any>):m.Children {
    window.document.title = this.getPageTitle();
    return this.render(vnode);
  }
  abstract getPageTitle():string;
  abstract render(vnode:m.Vnode<A, any>):m.Children;
}
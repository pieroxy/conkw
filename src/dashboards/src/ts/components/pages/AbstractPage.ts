import m from 'mithril';


export abstract class AbstractPage<A> implements m.ClassComponent<A> {
  public view(vnode:m.Vnode<A, any>):void|m.Children {
    window.document.title = this.getPageTitle();
    return this.render(vnode);
  }
  abstract getPageTitle():string;
  abstract render(vnode:m.Vnode<A, any>):void|m.Children;
}
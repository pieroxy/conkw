import m = require('mithril');

export class Test implements m.Component<any, any> {
  view(vnode:m.Vnode<any, any>):m.Children {
    if (vnode==null) {
      return m("", "vnode is null");
    }
    return m("", "Hello world");
  }
}
m.mount(document.body, Test)
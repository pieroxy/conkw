import m from 'mithril';
import { Routing } from '../../utils/navigation/Routing';

export class Link implements m.ClassComponent<LinkAttrs> {
  view({attrs,children}:m.Vnode<LinkAttrs>) {
    return m("a", {
      href:"#",
      onclick:() => {
        Routing.goToScreen(attrs.target)
        return false;
      },
      className:attrs.className ? attrs.className : "",
      title:attrs.tooltip ? attrs.tooltip : ""
    }, children)
  }
}

export interface LinkAttrs {
  target:string, // This is the route to get to
  className?:string,
  tooltip?:string,
}
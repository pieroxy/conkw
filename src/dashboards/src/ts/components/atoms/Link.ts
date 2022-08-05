import m from 'mithril';
import { Routing } from '../../utils/navigation/Routing';

export class Link implements m.ClassComponent<LinkAttrs> {
  view({attrs,children}:m.Vnode<LinkAttrs>) {
    return m("a", {
      href:"#",
      onclick:() => {
        if (typeof attrs.target === "string") Routing.goToScreen(attrs.target);
        else if (typeof attrs.target === "function") attrs.target();
        return false;
      },
      className:attrs.className ? attrs.className : "",
      title:attrs.tooltip ? attrs.tooltip : ""
    }, children)
  }
}

export interface LinkAttrs {
  target:string|(()=>void), // This is the route to get to
  className?:string,
  tooltip?:string,
}
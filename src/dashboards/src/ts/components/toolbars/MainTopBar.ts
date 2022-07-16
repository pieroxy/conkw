import m from 'mithril';

import { Children } from "mithril";

export class MainTopBar implements m.ClassComponent<any> {
  
  view(): void | Children {
    return m(".topbar", [
      m("img.logo", {
        src:"/images/medium_logo_transparent_50prc.png"
      })
    ])
  }
}
import m from 'mithril';

import { Children } from "mithril";
import { Api } from '../../utils/api/Api';

export class MainTopBar implements m.ClassComponent<any> {
  
  view(): void | Children {
    return m(".topbar", [
      m("img.logo", {
        src:"/images/medium_logo_transparent_50prc.png"
      }),
      Api.isWaiting() ? m(".loader", "Waiting") : null
    ])
  }
}
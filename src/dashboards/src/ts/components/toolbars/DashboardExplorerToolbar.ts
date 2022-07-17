import m from 'mithril';

import { Children } from "mithril";
import { Notification, Notifications, NotificationsClass, NotificationsType } from '../../utils/Notifications';
import { Button } from '../forms/Button';
import { Form } from '../forms/Form';
import { TextFieldInput } from '../forms/TextFieldInput';

export class DashboardExplorerToolbar implements m.ClassComponent<DashboardExplorerToolbarAttrs> {
  private filter:string="";
  
  view({attrs}:m.Vnode<DashboardExplorerToolbarAttrs>): void | Children {
    return m(".toolbar", [
      m(".inputlabel", "Search"),
      m(TextFieldInput, {
        refHolder:this,
        refProperty:"filter",
        form:new Form(),
        onchange:()=>attrs.filter(this.filter),
        onenter:()=>{}
      }),
      m(".right", 
        m(Button, {
          action:() => {
            let go=() => {
              Notifications.dismiss(notif);
            };
            let data={str:""};
            let notif = new Notification(
              NotificationsClass.LOGIN, 
              NotificationsType.INFO, 
              [
                m(".inputlabel", "New dashboard name:"),
                m(TextFieldInput, {
                  refHolder:data,
                  refProperty:"str",
                  onenter:go,
                  focus:true
                }),
                m("", m(".floatright", m(Button, {
                  action:go
                }, "Créer"))),
              ], 
              -1);
            Notifications.addNotification(notif)
          }
        }, "+")
      )
    ])
  }
}

export interface DashboardExplorerToolbarAttrs {
  filter: (s:string)=>void;
}
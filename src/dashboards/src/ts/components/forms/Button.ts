import m from 'mithril';

import { Children } from "mithril";
import { Form } from './Form';

export class Button implements m.ClassComponent<ButtonInputAttrs> {
  
  view({attrs,children}: m.Vnode<ButtonInputAttrs>): void | Children {
    return m(
      "button" + (attrs.secondary ? ".secondary":""), 
      {
        type:"button",
        onclick: () => {
          if (attrs.form) {
            if (!attrs.form.validate()) return;
          }
          attrs.action();
        },
        
      }, m(".innerbutton", children));
  }
}

export interface ButtonInputAttrs {
  action:()=>void;
  form?:Form;
  secondary?:boolean;
}
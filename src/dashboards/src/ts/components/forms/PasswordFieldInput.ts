import m from 'mithril';

import { Children } from "mithril";
import { GenericInput } from './GenericInput';
import { HtmlInputEvent } from './HtmlInputEvent';
import { TextInputAttrs } from './TextFieldInput';

export class PasswordFieldInput extends GenericInput<string, TextInputAttrs> {
  
  render({attrs}: m.Vnode<TextInputAttrs>): void | Children {
    return m(
      "input" + this.getErrorClass(), 
      {
        type:"password",
        id:attrs.id?attrs.id:null,
        oninput: (e:HtmlInputEvent) => {
          this.setValue(e.target.value);
          this.computeErrorState();
        },
        autocapitalize:"none",
        value: this.getValueAsString(),
        onkeyup: (e: KeyboardEvent) => {
          if (e && e.keyCode == 13) {
            attrs.onenter();
          }
          return true;
        }
      });
  }

  isEmpty(): boolean {
    return !this.getValueAsString()
  }
}
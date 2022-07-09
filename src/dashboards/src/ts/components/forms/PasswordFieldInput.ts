import m from 'mithril';

import { Children } from "mithril";
import { GenericInput } from './GenericInput';
import { HtmlInputEvent } from './HtmlInputEvent';
import { TextInputAttrs } from './TextFieldInput';

export class PasswordFieldInput extends GenericInput<string, TextInputAttrs> {
  
  render(_vnode: m.Vnode<TextInputAttrs>): void | Children {
    return m(
      "input" + this.getErrorClass(), 
      {
        type:"password",
        oninput: (e:HtmlInputEvent) => {
          this.setValue(e.target.value);
          this.computeErrorState();
        },
        value: this.getValueAsString()
      });
  }

  isEmpty(): boolean {
    return !this.getValueAsString()
  }
}
import m from 'mithril';

import { Children } from "mithril";
import { GenericInput } from './GenericInput';
import { GenericInputAttrs } from './GenericInputAttrs';
import { HtmlInputEvent } from './HtmlInputEvent';

export class TextFieldInput extends GenericInput<string, TextInputAttrs> {
  render({attrs}: m.Vnode<TextInputAttrs>): void | Children {
    let params:any = {
      type:"text",
      oninput: (e:HtmlInputEvent) => {
        this.setValue(e.target.value);
        this.computeErrorState();
      },
      value: this.getValueAsString(),
      onkeyup: (e: KeyboardEvent) => {
        if (e && e.keyCode == 13) {
          attrs.onenter();
        }
        return true;
      }
    }
    if (attrs.placeholder) params.placeholder = attrs.placeholder;
    if (attrs.id) params.id = attrs.id;

    return m(
      "input" + this.getErrorClass(), 
      params
    );
  }

  isEmpty(): boolean {
    return !this.getValueAsString()
  }
}

export interface TextInputAttrs extends GenericInputAttrs<string> {
  placeholder?: string
  onenter:()=>void;
}
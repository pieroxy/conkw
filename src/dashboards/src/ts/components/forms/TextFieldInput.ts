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
      value: this.getValueAsString()
    }
    if (attrs.placeholder) params.placeholder = attrs.placeholder;
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
}
import m from 'mithril';

import { Children } from "mithril";
import { GenericInput } from './GenericInput';
import { GenericInputAttrs } from './GenericInputAttrs';
import { HtmlInputEvent } from './HtmlInputEvent';

export class TextFieldInput extends GenericInput<string, TextInputAttrs> {
  
  render(_vnode: m.Vnode<TextInputAttrs>): void | Children {
    return m(
      "input", 
      {
        type:"text",
        oninput: (e:HtmlInputEvent) => {this.setValue(e.target.value)},
        value: this.getValueAsString()
      });
  }

  isEmpty(): boolean {
    return !this.getValueAsString()
  }
}

export interface TextInputAttrs extends GenericInputAttrs<string> {
}
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
      autocapitalize:"none",
      onkeyup: (e: KeyboardEvent) => {
        if (e.code) {
          if (e.code == 'Enter' || e.code == 'NumpadEnter') {
            attrs.onenter();
          }
          if (e.code == 'Escape') {
            if (attrs.onescape) attrs.onescape();
          }
        } else {
          if (e.keyCode == 13) {
            attrs.onenter();
          }
          if (e.keyCode == 27) {
            if (attrs.onescape) attrs.onescape();
          }
        }
        return true;
      }
    }
    if (attrs.placeholder) params.placeholder = attrs.placeholder;
    if (attrs.id) params.id = attrs.id;

    return m(
      "input" + this.getErrorClass() + (attrs.search ? ".searchbg":""), 
      params
    );
  }

  isEmpty(): boolean {
    return !this.getValueAsString()
  }

  oncreate(vnode:m.VnodeDOM<TextInputAttrs>) {
    if (vnode.attrs.focus)
      (<any>vnode.dom).focus();
  }
}

export interface TextInputAttrs extends GenericInputAttrs<string> {
  placeholder?: string
  onenter:()=>void;
  onescape?:()=>void;
  focus?:boolean;
  search?:boolean;
}
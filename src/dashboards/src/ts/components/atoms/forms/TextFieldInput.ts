import m from 'mithril';

import { Children } from "mithril";
import { GenericInput } from './GenericInput';
import { GenericInputAttrs } from './GenericInputAttrs';
import { HtmlInputEvent } from './HtmlInputEvent';

export class TextFieldInput extends GenericInput<string, VisibleTextInputAttrs> {
  render({attrs}: m.Vnode<VisibleTextInputAttrs>): void | Children {
    let params:any = attrs.params;
    if (!params) params = {};
    params.type="text";
    params.oninput= (e:HtmlInputEvent) => {
      this.setValue(e.target.value);
      this.computeErrorState();
      if (attrs.onchange) attrs.onchange();
    },
    params.value= this.getValueAsString(),
    params.autocapitalize="none",
    params.onkeyup= (e: KeyboardEvent) => {
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
    if (attrs.placeholder) params.placeholder = attrs.placeholder;
    if (attrs.id) params.id = attrs.id;
    if (attrs.spellcheck !== undefined) params.spellcheck = !!attrs.spellcheck;
    if (attrs.disabled) params.disabled = !!attrs.disabled;
    if (attrs.status) {
      params.onfocus = () => {this.status = attrs.status;};
      params.onblur = () => {this.status = undefined;};
    }

    return [
      m(
        "input" + this.getErrorClass() + (attrs.search ? ".searchbg":"") + (attrs.className ? attrs.className:""), 
        params
      ),
      this.getStatusIcon(attrs.status),
      (this.status && this.status.message) ? [
        m("br"),
        m(".inputStatusLabel", this.status.message)
      ] : null
    ];
  }

  isEmpty(): boolean {
    return !this.getValueAsString()
  }

  oncreate(vnode:m.VnodeDOM<VisibleTextInputAttrs>) {
    if (vnode.attrs.focus)
      (<any>vnode.dom).focus();
  }
}

export interface TextInputAttrs extends GenericInputAttrs<string> {
  placeholder?: string
  onenter:()=>void;
  onescape?:()=>void;
  focus?:boolean;
  className?:string;
}

export interface VisibleTextInputAttrs extends TextInputAttrs {
  spellcheck:boolean;
  search?:boolean;
}
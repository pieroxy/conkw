import m from 'mithril';

import { Children } from "mithril";
import { IdLabelPair } from '../../auto/pieroxy-conkw';
import { GenericInput } from './GenericInput';
import { GenericInputAttrs } from './GenericInputAttrs';
import { HtmlInputEvent } from './HtmlInputEvent';

export class SelectInput extends GenericInput<string, SelectInputAttrs> {
  render({attrs}: m.Vnode<SelectInputAttrs>): void | Children {
    let params:any = {
      oninput: (e:HtmlInputEvent) => {
        this.setValue(e.target.value);
        if (attrs.onchange) attrs.onchange();
      },
      onkeyup: (e: KeyboardEvent) => {
        if (e.code) {
          if (e.code == 'Enter' || e.code == 'NumpadEnter') {
            attrs.onenter();
          }
        } else {
          if (e.keyCode == 13) {
            attrs.onenter();
          }
        }
        return true;
      }
    }
    if (attrs.id) params.id = attrs.id;
    if (attrs.disabled) params.disabled = !!attrs.disabled;

    return m(
      "select" + this.getErrorClass() + (attrs.className ? attrs.className:""), 
      params,
      attrs.values.map(v => {
        let options:any = {value:v.id};
        if (v.id === attrs.refHolder[attrs.refProperty]) options["selected"] = true;
        return m("option", options, v.label)
      })
    );
  }

  isEmpty(): boolean {
    return !this.getValueAsString()
  }

  oncreate(vnode:m.VnodeDOM<SelectInputAttrs>) {
    if (vnode.attrs.focus)
      (<any>vnode.dom).focus();
  }
}

export interface SelectInputAttrs extends GenericInputAttrs<string> {
  onenter:()=>void;
  focus?:boolean;
  className?:string;
  values:IdLabelPair[];
}
import m from 'mithril';

import { Children } from "mithril";
import { GenericInput } from './GenericInput';
import { HtmlInputEvent } from './HtmlInputEvent';
import { SelectInputAttrs } from './SelectInput';

export class MultipleSelectInput extends GenericInput<string, SelectInputAttrs> {
  render({attrs}: m.Vnode<SelectInputAttrs>): void | Children {
    let params:any = {
      multiple:true,
      oninput: (e:HtmlInputEvent) => {
        attrs.refHolder[attrs.refProperty] = this.getValue(<HTMLSelectElement>e.target);
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

    return [
        m(
        "select" + this.getErrorClass() + (attrs.className ? attrs.className:""), 
        params,
        attrs.values.map(v => {
          let options:any = {value:v.id};
          if (v.id === attrs.refHolder[attrs.refProperty]) options["selected"] = true;
          return m("option", options, v.label)
        })
      ),
      GenericInput.getStatusIcon("selectStatusIcon", attrs.status),
    ]
  }

  isEmpty(): boolean {
    return !this.getValueAsString()
  }

  oncreate(vnode:m.VnodeDOM<SelectInputAttrs>) {
    if (vnode.attrs.focus)
      (<any>vnode.dom).focus();
  }

  getValue(select:HTMLSelectElement):string[] {
    let value:string[] = [];
    for (let i=0 ; i<select.options.length ; i++) {
      if (select.options.item(i)?.selected) {
        let item = select.options.item(i);
        if (item && item.value) value.push(item.value);
      }
    }
    return value;
  }
}

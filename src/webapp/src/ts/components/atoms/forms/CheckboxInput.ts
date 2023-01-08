import m from 'mithril';

import { Children } from "mithril";
import { GenericInput } from './GenericInput';
import { GenericInputAttrs } from './GenericInputAttrs';
import { HtmlInputEvent } from './HtmlInputEvent';

export class CheckboxInput extends GenericInput<boolean, CheckboxInputAttrs> {
  private attrs: CheckboxInputAttrs;

  oninit() {
    this.convertValueFromString = (s:string):boolean => {
      return s === "true";
    }
    this.convertValueToString = (s:boolean):string => {
      return s ? "true" : "false";
    }
  }

  render({attrs,children}: m.Vnode<CheckboxInputAttrs>): Children {
    this.attrs = attrs;
    let params:any = {
      type:"checkbox",
      oninput: (e:HtmlInputEvent) => {
        this.setValue(""+(<HTMLInputElement>e.target).checked);
        this.computeErrorState();
        if (attrs.onchange) attrs.onchange();
      },
    }
    if (attrs.refHolder[attrs.refProperty]) params.checked="true";

    return m(
      "label.checkbox" + this.getErrorClass(), 
      [
        m("input.checkbox", params),
        children
      ]
    );
  }

  public isEmpty():boolean {
    return typeof this.attrs.refHolder[this.attrs.refProperty] !== 'boolean';
  }

  convertValueToString = (value:boolean) => ""+value;
  convertValueFromString = (value:String) => value === 'true';
}

export interface CheckboxInputAttrs extends GenericInputAttrs<boolean> {
}
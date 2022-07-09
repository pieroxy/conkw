import m from 'mithril';

import { _NoLifecycle, Vnode, Children } from "mithril";
import { GenericInput } from "./GenericInput";
import { GenericInputAttrs } from "./GenericInputAttrs";
import { HtmlInputEvent } from './HtmlInputEvent';

export abstract class TextFieldInput<V, T extends TextInputAttrs<V>> extends GenericInput<V, T> {
  abstract onEnter(value:V):void;
  
  render(_vnode: Vnode<T, _NoLifecycle<any>>): void | Children {
    return m(
      "input", 
      {
        type:this.getInputType(),
        oninput: (e:HtmlInputEvent) => {this.setValue(e.target.value)},
        value: this.getValueAsString()
      });
  }

  isEmpty():boolean {
    return !!this.getValueAsString() && !!this.getValueAsString().length;
  }

  getInputType():string {
    return "text";
  }
};

export interface TextInputAttrs<V> extends GenericInputAttrs<V> {
}
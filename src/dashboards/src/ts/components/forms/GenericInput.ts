import m from 'mithril';

import { _NoLifecycle, Vnode, Children } from "mithril";
import { Form } from './Form';
import { GenericInputAttrs } from "./GenericInputAttrs";

export abstract class GenericInput<VT, T extends GenericInputAttrs<VT>> implements m.ClassComponent<T> {
  abstract isEmpty():boolean;
  abstract render(vnode: Vnode<T>): void | Children;

  
  private refHolder:any;
  private refProperty:string|number;
  private requiredMessage?:string;
  private highilighted:boolean=false;
  private convertValueToString:(value:VT)=>string;
  private convertValueFromString:(value:String) => VT;

  protected form:Form

  getErrorClass() {
    return this.highilighted ? ".error" : "";
  }

  oninit(vnode: Vnode<T>) {
    if (vnode.attrs.format) {
      this.convertValueToString = vnode.attrs.format;
    } else {
      this.convertValueToString = (v) => ""+v;
    }
    if (vnode.attrs.parse) {
      this.convertValueFromString = vnode.attrs.parse;
    } else {
      this.convertValueFromString = (s) => <VT><any>s;
    }
    this.refHolder = vnode.attrs.refHolder;
    this.refProperty = vnode.attrs.refProperty;
    if (vnode.attrs.form) {
      this.form = vnode.attrs.form;
      this.form.add(this);
    }
  }
  
  view(vnode: Vnode<T>): void | Children {
    this.refHolder = vnode.attrs.refHolder;
    this.refProperty = vnode.attrs.refProperty;
    this.requiredMessage = vnode.attrs.requiredMessage;
    return this.render(vnode);
  }

  isRequired():boolean {
    return this.requiredMessage !== undefined;
  }
  getRequiredMessage():string {
    return this.requiredMessage||"";
  }
  isInErrorState() {
    return this.isRequired() && this.isEmpty();
  }
  computeErrorState() {
    this.setHighlight(this.isInErrorState());
  }
  setHighlight(b:boolean):void {
    this.highilighted = b;
  }
  isHighlight():boolean {
    return this.highilighted;
  }

  protected setValue(value:string) {
    this.refHolder[this.refProperty] = this.convertValueFromString(value);
  }
  protected getValueAsString():string {
    return this.convertValueToString(this.refHolder[this.refProperty]);
  }
};

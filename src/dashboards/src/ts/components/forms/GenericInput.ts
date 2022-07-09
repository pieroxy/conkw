import m from 'mithril';

import { _NoLifecycle, Vnode, Children } from "mithril";
import { GenericInputAttrs } from "./GenericInputAttrs";

export abstract class GenericInput<VT, T extends GenericInputAttrs<VT>> implements m.ClassComponent<T> {
  abstract isEmpty():boolean;
  abstract render(_vnode: Vnode<T, _NoLifecycle<any>>): void | Children;

  
  private refHolder:any;
  private refProperty:string|number;
  private requiredMessage?:string;
  private highilighted:boolean=false;
  private convertValueToString:(value:VT)=>string;
  private convertValueFromString:(value:String) => VT;

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

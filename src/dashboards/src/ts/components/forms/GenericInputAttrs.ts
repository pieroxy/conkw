import m = require('mithril');

import { Form } from "./Form";

export interface GenericInputAttrs<T> {
  params?:m.Attributes,
  refHolder: any,
  refProperty: string|number,
  requiredMessage?: string,
  format?: (s:T) => string,
  parse?: (s:string) => T,
  form?: Form,
  onchange?: ()=>void,
  id?:string,
  disabled?:boolean
}

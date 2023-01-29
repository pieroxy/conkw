import m from 'mithril';

import { Children } from "mithril";
import { StatusMessageInterface } from '../../../utils/types';
import { Form } from './Form';
import { SelectInput } from './SelectInput';
import { NumberFieldInput } from './TextFieldInput';

export class DelayInput implements m.ClassComponent<DelayInputAttrs> {
  number:string;
  unit:string;

  view({attrs}: m.Vnode<DelayInputAttrs>): void | Children {
    this.readValue(attrs);
    return m("table.delayInput", attrs.params, m("tr", [ 
      m("td",
        m(NumberFieldInput, {
          refHolder: this,
          refProperty: "number",
          requiredMessage: attrs.requiredMessage,
          form: attrs.form,
          disabled:attrs.disabled,
          params:{size:3},
          onenter:()=>{},
          spellcheck:false,
          onchange:()=>{
            attrs.refHolder[attrs.refProperty] = this.number+this.unit;
          },
          status:attrs.status
        })
      ),
      m("td", 
        m(SelectInput, {
          onenter:()=>{},
          values:[
            {id:"ms", label:"milliseconds"},
            {id:"s", label:"seconds"},
            {id:"m", label:"minutes"},
            {id:"h", label:"hours"},
            {id:"d", label:"days"},
            {id:"y", label:"years"},
          ],
          refHolder: this,
          refProperty: "unit",
          requiredMessage: attrs.requiredMessage,
          form: attrs.form,
          onchange:()=>{
            if (this.number !== "") {
              attrs.refHolder[attrs.refProperty] = this.number+this.unit;
            }
          },
          disabled:attrs.disabled,
        })
      )
    ]));
  }

  readValue(attrs:DelayInputAttrs) {
    let valueString = attrs.refHolder[attrs.refProperty];
    if (!valueString) {
      this.number = "";
      if (!this.unit) this.unit = 's';
    } else {
      if (valueString.endsWith("ms")) {
        this.number = ""+this.parse(valueString, 2);
        this.unit='ms';
      } else if (valueString.endsWith("s")) {
        this.number = ""+this.parse(valueString, 1);
        this.unit='s';
      } else if (valueString.endsWith("m")) {
        this.number = ""+this.parse(valueString, 1);
        this.unit='m';
      } else if (valueString.endsWith("h")) {
        this.number = ""+this.parse(valueString, 1);
        this.unit='h';
      } else if (valueString.endsWith("d")) {
        this.number = ""+this.parse(valueString, 1);
        this.unit='d';
      } else if (valueString.endsWith("y")) {
        this.number = ""+this.parse(valueString, 1);
        this.unit='y';
      } else {
        this.number = "1";
        this.unit = 's';
      }
    }
  }
  parse(s: string, suffixSize: number): number {
    return parseInt(s.substring(0, s.length-suffixSize))
  }
}

export interface DelayInputAttrs {
  params?:any,
  refHolder: any,
  refProperty: string|number,
  requiredMessage?: string,
  form?: Form,
  onchange?: ()=>void,
  disabled?:boolean,
  status?:StatusMessageInterface
}
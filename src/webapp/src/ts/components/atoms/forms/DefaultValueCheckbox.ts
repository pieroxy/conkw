import m from 'mithril';

import { CheckboxInput } from './CheckboxInput';

export class DefaultValueCheckbox implements m.Component<any, DefaultValueCheckboxAttrs> {
  private state:boolean;

  private setState(attrs: DefaultValueCheckboxAttrs) {
    this.state=attrs.refHolder[attrs.refProperty] === attrs.emptyValue;
  }

  view({attrs}: m.Vnode<DefaultValueCheckboxAttrs>): m.Children {
    this.setState(attrs);
    return m(".defaultValue", [
      m(CheckboxInput, {
        refHolder:this,
        refProperty:'state',
        onchange:() => {
          if (this.state) {
            attrs.refHolder[attrs.refProperty] = attrs.emptyValue;
          }
          m.redraw()
        }
      }),
      m("label", {for:""}, 
        "Default: ",
        m("span.monospace", attrs.value)
      )
    ])
  }

}

export interface DefaultValueCheckboxAttrs {
  refHolder:any;
  refProperty:string;
  value:any;
  emptyValue?:any;
}
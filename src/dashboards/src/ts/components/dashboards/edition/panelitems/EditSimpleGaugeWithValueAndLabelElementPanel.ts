import m from 'mithril';
import { ExpressionClass, SimpleGaugeWithValueAndLabelElement } from '../../../../auto/pieroxy-conkw';
import { CheckboxInput } from '../../../atoms/forms/CheckboxInput';
import { TextFieldInput } from '../../../atoms/forms/TextFieldInput';
import { EditSimpleGaugePanel } from '../atoms/EditSimpleGaugePanel';
import { EditSimpleLabelPanel } from '../atoms/EditSimpleLabelPanel';
import { EditSimpleSiLabelPanel } from '../atoms/EditSimpleSiLabelPanel';

export class EditSimpleGaugeWithValueAndLabelElementPanel implements m.ClassComponent<EditSimpleGaugeWithValueAndLabelElementPanelAttrs> {
  private labelIsStatic:boolean;

  view({attrs}: m.Vnode<EditSimpleGaugeWithValueAndLabelElementPanelAttrs, this>): void | m.Children {
    this.labelIsStatic = attrs.element?.label.value.clazz === ExpressionClass.LITERAL;
    console.log((attrs.element?.label.value.clazz === ExpressionClass.LITERAL) + " " + attrs.element?.label.value.clazz + " === " +ExpressionClass.LITERAL);

    if (attrs.element) {
      let element = attrs.element;
      return [
        m(".title", ["Edit ", m("i", attrs.element.label.value.value)]),
        m(".editionPanel", [
          m(".group", [
            m(".groupTitle", "Simplifications"),
            m(CheckboxInput, {
              refHolder:this,
              refProperty:"labelIsStatic",
              onchange:() => {
                if (this.labelIsStatic) {
                  element.label.value.clazz = ExpressionClass.LITERAL;
                } else {
                  element.label.value.clazz = ExpressionClass.METRIC;
                }
              }
            }, "Label is static"),
          ]),
          m("br"),
          m(".group", [
            m(".groupTitle", "Definition"),
            this.labelIsStatic ?
              m(".inputWithLabel", [
                m(".label", "Label"),
                m(TextFieldInput, {
                  onenter:()=>{},
                  refHolder:element.label.value,
                  refProperty:"value",
                  spellcheck:false
                })
              ]) : 
              m(".group", [
                m(".groupTitle", "The label"),
                m(EditSimpleLabelPanel, {element:attrs.element.label}),
              ]),
          ]),




    

          m(".group", [
            m(".groupTitle", "The value"),
            m(EditSimpleSiLabelPanel, {element:attrs.element.value}),
          ]),
          m(".group", [
            m(".groupTitle", "The gauge"),
            m(EditSimpleGaugePanel, {element:attrs.element.gauge}),
          ])
        ]),
      ];
    }
    return m("");
  }

}

export interface EditSimpleGaugeWithValueAndLabelElementPanelAttrs {
  element?:SimpleGaugeWithValueAndLabelElement;
}


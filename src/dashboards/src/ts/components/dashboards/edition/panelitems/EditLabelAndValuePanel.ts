import m from 'mithril';
import { ExpressionClass, ExpressionValueType, LabelAndValueElement, WarningDirective } from '../../../../auto/pieroxy-conkw';
import { CheckboxInput } from '../../../atoms/forms/CheckboxInput';
import { TextFieldInput } from '../../../atoms/forms/TextFieldInput';
import { EditSimpleLabelPanel } from '../atoms/EditSimpleLabelPanel';
import { EditSimpleNumericValuePanel } from '../atoms/EditSimpleNumericValuePanel';

export class EditLabelAndValuePanel implements m.ClassComponent<EditLabelAndValuePanelAttrs> {
  private labelIsStatic:boolean;

  view({attrs}: m.Vnode<EditLabelAndValuePanelAttrs, this>): void | m.Children {
    if (attrs.element) {
      let element = attrs.element;

      if (!element.value.error) {
        element.value.error = {
          clazz:ExpressionClass.LITERAL,
          namespace:"",
          type:ExpressionValueType.NUMERIC,
          value:"0",
          directive:{test:WarningDirective.VALUEABOVE}
        }
      }

      this.labelIsStatic = element.label.value.clazz === ExpressionClass.LITERAL;
      if (!element.value.error.directive) element.value.error.directive = {};

  
      return [
        m(".title", ["Edit ", m("i", element.label.value.value)]),
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
            m(".groupTitle", "Simple Definition"),
            this.labelIsStatic ?
              m(".inputWithLabel", [
                m(".label", "Label"),
                m(TextFieldInput, {
                  onenter:()=>{},
                  refHolder:element.label.value,
                  refProperty:"value",
                  spellcheck:false
                })
              ])
            : null,
          ]),
          m(".group", [
            m(".groupTitle", "Value"),
            m(EditSimpleNumericValuePanel, {element:attrs.element.value.value}),
          ]),
          this.labelIsStatic ? null :
            m(".group", [
              m(".groupTitle", "The label"),
              m(EditSimpleLabelPanel, {element:attrs.element.label}),
            ]),
        ]),
      ];
    }
    return m("Loading ...");
  }

}

export interface EditLabelAndValuePanelAttrs {
  element?:LabelAndValueElement;
}


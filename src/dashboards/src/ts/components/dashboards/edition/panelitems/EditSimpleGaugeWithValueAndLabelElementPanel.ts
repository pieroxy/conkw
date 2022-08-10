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
    if (attrs.element?.valueIsGauge) {
      // Every change ends up in a redraw, so it is easier to make this update here.
      // Will (should?) move it when the refacto simplifications are done.
      attrs.element.value.value.clazz = attrs.element.gauge.value.clazz;
      attrs.element.value.value.namespace = attrs.element.gauge.value.namespace;
      attrs.element.value.value.type = attrs.element.gauge.value.type;
      attrs.element.value.value.value = attrs.element.gauge.value.value;
    }

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
            m(CheckboxInput, {
              refHolder:attrs.element,
              refProperty:"valueIsGauge",
            }, "Simple value"),
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
              ])
            : 
              m(".group", [
                m(".groupTitle", "The label"),
                m(EditSimpleLabelPanel, {element:attrs.element.label}),
              ]),
              attrs.element.valueIsGauge ?
              m(".inputWithLabel", [
                m(".label", "Unit"),
                m(TextFieldInput, {
                  onenter:()=>{},
                  refHolder:element.value,
                  refProperty:"unit",
                  spellcheck:false,
                  params: {
                    size:3
                  }
                })
              ])
            :
              m(".group", [
                m(".groupTitle", "The value"),
                m(EditSimpleSiLabelPanel, {element:attrs.element.value}),
              ]),
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


import m from 'mithril';
import { ExpressionClass, SimpleGaugeWithValueAndLabelElement } from '../../../../auto/pieroxy-conkw';
import { CheckboxInput } from '../../../atoms/forms/CheckboxInput';
import { TextFieldInput } from '../../../atoms/forms/TextFieldInput';
import { EditSimpleLabelPanel } from '../atoms/EditSimpleLabelPanel';
import { EditSimpleNumericValuePanel } from '../atoms/EditSimpleNumericValuePanel';
import { EditSimpleSiLabelPanel } from '../atoms/EditSimpleSiLabelPanel';

export class EditSimpleGaugeWithValueAndLabelElementPanel implements m.ClassComponent<EditSimpleGaugeWithValueAndLabelElementPanelAttrs> {
  private labelIsStatic:boolean;
  private maxIsStatic:boolean;
  private minIsStatic:boolean;

  view({attrs}: m.Vnode<EditSimpleGaugeWithValueAndLabelElementPanelAttrs, this>): void | m.Children {
    this.labelIsStatic = attrs.element?.label.value.clazz === ExpressionClass.LITERAL;
    this.minIsStatic = attrs.element?.gauge.min.clazz === ExpressionClass.LITERAL;
    this.maxIsStatic = attrs.element?.gauge.max.clazz === ExpressionClass.LITERAL;
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
              refHolder:this,
              refProperty:"minIsStatic",
              onchange:() => {
                if (this.minIsStatic) {
                  element.gauge.min.clazz = ExpressionClass.LITERAL;
                } else {
                  element.gauge.min.clazz = ExpressionClass.METRIC;
                }
              }
            }, "Min is static"),
            m(CheckboxInput, {
              refHolder:this,
              refProperty:"maxIsStatic",
              onchange:() => {
                if (this.maxIsStatic) {
                  element.gauge.max.clazz = ExpressionClass.LITERAL;
                } else {
                  element.gauge.max.clazz = ExpressionClass.METRIC;
                }
              }
            }, "Max is static"),
            m(CheckboxInput, {
              refHolder:attrs.element,
              refProperty:"valueIsGauge",
            }, "Simple value"),
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
            : null,
            this.minIsStatic ?
              m(".inputWithLabel", [
                m(".label", "Minimum"),
                m(TextFieldInput, {
                  onenter:()=>{},
                  refHolder:element.gauge.min,
                  refProperty:"value",
                  spellcheck:false,
                  params: {
                    size:10
                  }
                })
              ])
            : null,
      
            this.maxIsStatic ?
              m(".inputWithLabel", [
                m(".label", "Maximum"),
                m(TextFieldInput, {
                  onenter:()=>{},
                  refHolder:element.gauge.max,
                  refProperty:"value",
                  spellcheck:false,
                  params: {
                    size:10
                  }
                })
              ])
            : null,
            m(".inputWithLabel", [
              m(".label", "Stale after (s)"),
              m(TextFieldInput, {
                onenter:()=>{},
                refHolder:attrs.element.gauge,
                refProperty:"staleDelay",
                spellcheck:false,
                params: {
                  size:3
                },
                onchange() {
                  element.value.staleDelay = element.gauge.staleDelay
                }
              })
            ])
          ]),
          m(".group", [
            m(".groupTitle", "Gauge value"),
            m(EditSimpleNumericValuePanel, {element:attrs.element.gauge.value}),
          ]),
          this.labelIsStatic ? null :
            m(".group", [
              m(".groupTitle", "The label"),
              m(EditSimpleLabelPanel, {element:attrs.element.label}),
            ]),
            attrs.element.valueIsGauge ? null :
            m(".group", [
              m(".groupTitle", "The value"),
              m(EditSimpleSiLabelPanel, {element:attrs.element.value}),
            ]),
          this.minIsStatic ? null :
            m(".group", [
              m(".groupTitle", "Minimum"),
              m(EditSimpleNumericValuePanel, {element:attrs.element.gauge.min}),
            ]),
    
          this.maxIsStatic ? null :
            m(".group", [
              m(".groupTitle", "Minimum"),
              m(EditSimpleNumericValuePanel, {element:attrs.element.gauge.max}),
            ]),

        ]),
      ];
    }
    return m("");
  }

}

export interface EditSimpleGaugeWithValueAndLabelElementPanelAttrs {
  element?:SimpleGaugeWithValueAndLabelElement;
}


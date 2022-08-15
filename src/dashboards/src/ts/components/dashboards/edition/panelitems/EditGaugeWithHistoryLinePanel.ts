import m from 'mithril'
import { ExpressionClass, ExpressionValueType, GaugeWithHistoryElement, WarningDirective } from '../../../../auto/pieroxy-conkw';
import { ModelUtils } from '../../../../utils/ModelUtils';
import { CheckboxInput } from '../../../atoms/forms/CheckboxInput';
import { SelectInput } from '../../../atoms/forms/SelectInput';
import { TextFieldInput } from '../../../atoms/forms/TextFieldInput';
import { EditSimpleLabelPanel } from '../atoms/EditSimpleLabelPanel';
import { EditSimpleNumericValuePanel } from '../atoms/EditSimpleNumericValuePanel';
import { EditSimpleSiLabelPanel } from '../atoms/EditSimpleSiLabelPanel';
import { EditThresoldPanel } from '../atoms/EditThresholdPanel';

export class EditGaugeWithHistoryLinePanel implements m.ClassComponent<EditGaugeWithHistoryLinePanelAttrs> {
  private labelIsStatic:boolean;
  private maxIsStatic:boolean;
  private minIsStatic:boolean;
  private thresholdIsStatic:boolean;

  oninit({attrs}: m.Vnode<EditGaugeWithHistoryLinePanelAttrs, this>):void {
    this.thresholdIsStatic = !attrs.element?.value.error || JSON.stringify(attrs.element?.gauge.error) === JSON.stringify(attrs.element?.value.error)
  }

  view({attrs}: m.Vnode<EditGaugeWithHistoryLinePanelAttrs, this>): void | m.Children {
    if (attrs.element) {
      let element = attrs.element;

      if (!element.value.error) {
        element.value.error = {
          clazz:ExpressionClass.LITERAL,
          namespace:"",
          type:ExpressionValueType.NUMERIC,
          value:"0",
          directive:WarningDirective.VALUEABOVE
        }
      }

      this.labelIsStatic = element.label.value.clazz === ExpressionClass.LITERAL;
      this.minIsStatic = element.gauge.min.clazz === ExpressionClass.LITERAL;
      this.maxIsStatic = element.gauge.max.clazz === ExpressionClass.LITERAL;
      this.thresholdIsStatic = element.value.error.clazz === ExpressionClass.LITERAL;
      if (element.valueIsGauge) {
        // Every change ends up in a redraw, so it is easier to make this update here.
        // Will (should?) move it when the refacto simplifications are done.
        ModelUtils.copyValueExpression(element.gauge.value, element.value.value);
      }
  
  
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
              refHolder:element,
              refProperty:"valueIsGauge",
            }, "Simple value"),
            m(CheckboxInput, {
              refHolder:this,
              refProperty:"thresholdIsStatic",
              onchange:() => {
                if (this.thresholdIsStatic) {
                  if (element.value.error) element.value.error.clazz = ExpressionClass.LITERAL;
                }
              }
            }, "Threshold is simple"),
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
            element.valueIsGauge ?
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
                refHolder:element.gauge,
                refProperty:"staleDelay",
                spellcheck:false,
                params: {
                  size:3
                },
                onchange() {
                  element.value.staleDelay = element.gauge.staleDelay
                }
              })
            ]),
            !this.thresholdIsStatic ? null : [
              m(".inputWithLabel", [
                m(".label", "Threshold"),
                m(SelectInput, {
                  onenter:()=>{},
                  refHolder:element.value.error,
                  refProperty:"directive",
                  values:[
                    {id:WarningDirective.VALUEABOVE,label:"Value is above"},
                    {id:WarningDirective.VALUEBELOW,label:"Value is below"}
                  ]
                }),
                m(TextFieldInput, {
                  onenter:()=>{},
                  refHolder:element.value.error,
                  refProperty:"value",
                  spellcheck:false,
                  params: {
                    size:5
                  },
                })
              ]),
            ]
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

          this.thresholdIsStatic ? null : 
            m(".group",
              m(".groupTitle", "Threshold for the value"),
              m(EditThresoldPanel, {element:element.value.error})
            )

        ]),
      ];
    }
    return m("Loading ...");
  }
}
export interface EditGaugeWithHistoryLinePanelAttrs {
  element?:GaugeWithHistoryElement;
}

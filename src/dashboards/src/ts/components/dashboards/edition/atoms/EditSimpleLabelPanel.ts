import m from 'mithril';
import { DynamicLabel, ExpressionClass, ExpressionValueType } from '../../../../auto/pieroxy-conkw';
import { MetricsReader } from '../../../../utils/api/MetricsReader';
import { CheckboxInput } from '../../../atoms/forms/CheckboxInput';
import { SelectInput } from '../../../atoms/forms/SelectInput';
import { TextFieldInput } from '../../../atoms/forms/TextFieldInput';
import { TextFieldInputWithSearch } from '../../../atoms/forms/TextFieldInputWithSearch';

export class EditSimpleLabelPanel implements m.ClassComponent<EditSimpleLabelPanelAttrs> {
  view({attrs}: m.Vnode<EditSimpleLabelPanelAttrs>): void | m.Children {
    attrs.element.value.type = ExpressionValueType.STRING;
    return [
      m(".inputWithLabel", [
        m(".label", "Class"),
        m(SelectInput, {
          onenter:()=>{},
          refHolder:attrs.element.value,
          refProperty:"clazz",
          values:[
            {id:ExpressionClass.LITERAL, label:"Literal"},
            {id:ExpressionClass.METRIC, label:"Simple metric"},
            {id:ExpressionClass.EXPRESSION, label:"Expression"},
          ]
        }),
      ]),
      m(".inputWithLabel", [
        m(".label", "Grabber"),
        m(SelectInput, {
          onenter:()=>{},
          className:".grabberList",
          refHolder:attrs.element.value,
          refProperty:"namespace",
          values:MetricsReader.getGrabbers(),
          disabled:attrs.element.value.clazz == ExpressionClass.LITERAL
        }),
      ]),
      m(".inputWithLabel", [
        m(".label", "Value"),
        attrs.element.value.clazz === ExpressionClass.METRIC ?
        m(TextFieldInputWithSearch, {
          onenter:()=>{},
          refHolder:attrs.element.value,
          refProperty:"value",
          values:MetricsReader.getStrMetricsList(attrs.element.value.namespace),
          spellcheck:false
        }) : 
        m(TextFieldInput, {
          onenter:()=>{},
          refHolder:attrs.element.value,
          refProperty:"value",
          spellcheck:false
        })
      ]),
      m(".inputWithLabel", [
        m(".label", "Stale after (s)"),
        m(TextFieldInput, {
          onenter:()=>{},
          refHolder:attrs.element,
          refProperty:"staleDelay",
          spellcheck:false,
          params: {
            size:3
          }
        }),
      ]),

      m("br"),
      m(CheckboxInput, {
        refHolder:this,
        refProperty:"tooltip"
      }, "Tooltip"),
      m(CheckboxInput, {
        refHolder:this,
        refProperty:"tooltip"
      }, "Stale"),
      m(CheckboxInput, {
        refHolder:this,
        refProperty:"tooltip"
      }, "Error"),
    ];
  }

}

export interface EditSimpleLabelPanelAttrs {
  element:DynamicLabel;
}

import m from 'mithril';
import { ExpressionClass, ExpressionValueType, ValueExpression } from '../../../auto/pieroxy-conkw';
import { MetricsReader } from '../../../utils/api/MetricsReader';
import { SelectInput } from '../../forms/SelectInput';
import { TextFieldInput } from '../../forms/TextFieldInput';
import { TextFieldInputWithSearch } from '../../forms/TextFieldInputWithSearch';

export class EditSimpleNumericValuePanel implements m.ClassComponent<EditSimpleNumericValuePanelAttrs> {
  view({attrs}: m.Vnode<EditSimpleNumericValuePanelAttrs>): void | m.Children {
    attrs.element.type = ExpressionValueType.NUMERIC;
    return [
      m(".inputWithLabel", [
        m(".label", "Grabber"),
        m(SelectInput, {
          onenter:()=>{},
          className:".grabberList",
          refHolder:attrs.element,
          refProperty:"namespace",
          values:MetricsReader.getGrabbers()
        }),
      ]),
      m(".inputWithLabel", [
        m(".label", "Class"),
        m(SelectInput, {
          onenter:()=>{},
          refHolder:attrs.element,
          refProperty:"clazz",
          values:[
            {id:ExpressionClass.LITERAL, label:"Literal"},
            {id:ExpressionClass.METRIC, label:"Simple metric"},
            {id:ExpressionClass.EXPRESSION, label:"Expression"},
          ]
        }),
      ]),
      m(".inputWithLabel", [
        m(".label", "Value"),
        attrs.element.clazz === ExpressionClass.METRIC ?
        m(TextFieldInputWithSearch, {
          onenter:()=>{},
          refHolder:attrs.element,
          refProperty:"value",
          values:MetricsReader.getNumMetricsList(attrs.element.namespace)
        }) : 
        m(TextFieldInput, {
          onenter:()=>{},
          refHolder:attrs.element,
          refProperty:"value",
        })
      ])
    ];
  }

}

export interface EditSimpleNumericValuePanelAttrs {
  element:ValueExpression;
}

import m from 'mithril';
import { ExpressionClass, ExpressionValueType, ValueExpression } from '../../../auto/pieroxy-conkw';
import { SelectInput } from '../../forms/SelectInput';
import { TextFieldInput } from '../../forms/TextFieldInput';

export class EditSimpleNumericValuePanel implements m.ClassComponent<EditSimpleNumericValuePanelAttrs> {
  view({attrs}: m.Vnode<EditSimpleNumericValuePanelAttrs>): void | m.Children {
    attrs.element.type = ExpressionValueType.NUMERIC;
    return [
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
        m(TextFieldInput, {
          onenter:()=>{},
          refHolder:attrs.element,
          refProperty:"value",
        }),
      ])
    ];
  }

}

export interface EditSimpleNumericValuePanelAttrs {
  element:ValueExpression;
}

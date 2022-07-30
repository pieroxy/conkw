import m from 'mithril';
import { DynamicLabel, ExpressionClass, ExpressionValueType } from '../../../auto/pieroxy-conkw';
import { MetricsReader } from '../../../utils/api/MetricsReader';
import { SelectInput } from '../../forms/SelectInput';
import { TextFieldInput } from '../../forms/TextFieldInput';

export class EditSimpleLabelPanel implements m.ClassComponent<EditSimpleLabelPanelAttrs> {
  view({attrs}: m.Vnode<EditSimpleLabelPanelAttrs>): void | m.Children {
    attrs.element.value.type = ExpressionValueType.STRING;
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
        m(".label", "Value"),
        m(TextFieldInput, {
          onenter:()=>{},
          refHolder:attrs.element.value,
          refProperty:"value",
          spellcheck:true
        }),
      ])
    ];
  }

}

export interface EditSimpleLabelPanelAttrs {
  element:DynamicLabel;
}

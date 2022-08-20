import m from 'mithril';
import { ExpressionClass, ExpressionValueType, WarningDirective } from '../../../../auto/pieroxy-conkw';
import { MetricsReader } from '../../../../utils/api/MetricsReader';
import { SelectInput } from '../../../atoms/forms/SelectInput';
import { TextFieldInput } from '../../../atoms/forms/TextFieldInput';
import { TextFieldInputWithSearch } from '../../../atoms/forms/TextFieldInputWithSearch';
import { EditSimpleNumericValuePanelAttrs } from './EditSimpleNumericValuePanel';

export class EditThresoldPanel implements m.ClassComponent<EditSimpleNumericValuePanelAttrs> {
  view({attrs}: m.Vnode<EditSimpleNumericValuePanelAttrs>): void | m.Children {
    attrs.element.type = ExpressionValueType.NUMERIC;
    if (!attrs.element.directive) attrs.element.directive = {};
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
        m(".label", "Grabber"),
        m(SelectInput, {
          onenter:()=>{},
          className:".grabberList",
          refHolder:attrs.element,
          refProperty:"namespace",
          values:MetricsReader.getGrabbers(),
          disabled: attrs.element.clazz == ExpressionClass.LITERAL
        }),
      ]),
      m(".inputWithLabel", [
        m(".label", "Directive"),
        m(SelectInput, {
          onenter:()=>{},
          refHolder:attrs.element.directive,
          refProperty:"test",
          values:[
            {id:WarningDirective.VALUEABOVE,label:"Value is above"},
            {id:WarningDirective.VALUEBELOW,label:"Value is below"},
            {id:WarningDirective.VALUECONTAINS,label:"Value (text) contains"},
            {id:WarningDirective.IS,label:"Value is"},
            {id:WarningDirective.ISNOT,label:"Value is not"},
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
          values:MetricsReader.getNumMetricsList(attrs.element.namespace),
          spellcheck:false
        }) : 
        m(TextFieldInput, {
          onenter:()=>{},
          refHolder:attrs.element,
          refProperty:"value",
          spellcheck:false
        })
      ])
    ];
  }

}


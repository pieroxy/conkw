import m from 'mithril';
import { SiPrefixedValue } from '../../../auto/pieroxy-conkw';
import { SelectInput } from '../../atoms/forms/SelectInput';
import { TextFieldInput } from '../../atoms/forms/TextFieldInput';
import { EditSimpleNumericValuePanel } from './EditSimpleNumericValuePanel';

export class EditSimpleSiLabelPanel implements m.ClassComponent<EditSimpleSiLabelPanelAttrs> {
  view({attrs}: m.Vnode<EditSimpleSiLabelPanelAttrs>): void | m.Children {
    return [
      m(EditSimpleNumericValuePanel, {element:attrs.element.value}),
      m(".inputWithLabel", [
        m(".label", "a K is ..."),
        m(SelectInput, {
          onenter:()=>{},
          refHolder:attrs.element,
          refProperty:"thousand",
          values:[
            {id:"1000", label:"1000"},
            {id:"1024", label:"1024"},
          ]
        }),
      ]),
      m(".inputWithLabel", [
        m(".label", "Unit"),
        m(TextFieldInput, {
          onenter:()=>{},
          refHolder:attrs.element,
          refProperty:"unit",
          params: {
            size:3
          },
          spellcheck:false
        }),
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
        })
      ])

    ];
  }

}

export interface EditSimpleSiLabelPanelAttrs {
  element:SiPrefixedValue;
}

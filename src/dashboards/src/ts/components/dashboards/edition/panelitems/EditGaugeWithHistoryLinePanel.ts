import m from 'mithril'
import { GaugeWithHistoryElement } from '../../../../auto/pieroxy-conkw';
import { TextFieldInput } from '../../../atoms/forms/TextFieldInput';
import { EditSimpleGaugePanel } from '../atoms/EditSimpleGaugePanel';
import { EditSimpleLabelPanel } from '../atoms/EditSimpleLabelPanel';
import { EditSimpleSiLabelPanel } from '../atoms/EditSimpleSiLabelPanel';

export class EditGaugeWithHistoryLinePanel implements m.ClassComponent<EditGaugeWithHistoryLinePanelAttrs> {
  view({attrs}: m.Vnode<EditGaugeWithHistoryLinePanelAttrs, this>): void | m.Children {
    if (attrs.element) {
      return [
        m(".title", ["Edit ", m("i", attrs.element.label.value.value)]),
        m(".editionPanel", [
          m(".group", [
            m(".groupTitle", "The label"),
            m(EditSimpleLabelPanel, {element:attrs.element.label}),
          ]),
          m(".group", [
            m(".groupTitle", "The value"),
            m(EditSimpleSiLabelPanel, {element:attrs.element.value}),
          ]),
          m(".group", [
            m(".groupTitle", "The gauge"),
            m(EditSimpleGaugePanel, {element:attrs.element.gauge}),
          ]),
          m(".group", [
            m(".groupTitle", "Specific properties"),
              m(".inputWithLabel", [
                m(".label", "Gauge number of lines:"),
                m(TextFieldInput, {
                  onenter:()=>{},
                  refHolder:attrs.element.gauge,
                  refProperty:"nbLinesHeight",
                  spellcheck:false,
                  params: {
                    size:3
                  }
                })
              ])
            ]),
          ])
      ];
    }
    return m("");
  }
}
export interface EditGaugeWithHistoryLinePanelAttrs {
  element?:GaugeWithHistoryElement;
}

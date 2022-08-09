import m from 'mithril';
import { SimpleGaugeWithValueAndLabelElement } from '../../../../auto/pieroxy-conkw';
import { EditSimpleGaugePanel } from '../atoms/EditSimpleGaugePanel';
import { EditSimpleLabelPanel } from '../atoms/EditSimpleLabelPanel';
import { EditSimpleSiLabelPanel } from '../atoms/EditSimpleSiLabelPanel';

export class EditSimpleGaugeWithValueAndLabelElementPanel implements m.ClassComponent<EditSimpleGaugeWithValueAndLabelElementPanelAttrs> {
  view({attrs}: m.Vnode<EditSimpleGaugeWithValueAndLabelElementPanelAttrs, this>): void | m.Children {
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


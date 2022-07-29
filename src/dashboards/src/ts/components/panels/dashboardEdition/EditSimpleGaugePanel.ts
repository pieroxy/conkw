import m from 'mithril';
import { SimpleGauge } from '../../../auto/pieroxy-conkw';
import { EditSimpleNumericValuePanel } from './EditSimpleNumericValuePanel';

export class EditSimpleGaugePanel implements m.ClassComponent<EditSimpleGaugePanelAttrs> {
  view({attrs}: m.Vnode<EditSimpleGaugePanelAttrs>): void | m.Children {
    return [
      m(".group", [
        m(".groupTitle", "Value"),
        m(EditSimpleNumericValuePanel, {element:attrs.element.value}),
      ]),
      m(".group", [
        m(".groupTitle", "Minimum"),
        m(EditSimpleNumericValuePanel, {element:attrs.element.min}),
      ]),
      m(".group", [
        m(".groupTitle", "Maximum"),
        m(EditSimpleNumericValuePanel, {element:attrs.element.max}),
      ]),
    ];
  }

}

export interface EditSimpleGaugePanelAttrs {
  element:SimpleGauge;
}

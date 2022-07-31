import m from 'mithril';
import { SimpleGaugeWithValueAndLabelPanel } from '../../../auto/pieroxy-conkw';
import { MetricsReader } from '../../../utils/api/MetricsReader';
import { EditDocumentIcon } from '../../icons/EditDocumentIcon';
import { SimpleGaugeWithValueAndLabelElementComponent } from '../../organisms/dashboards/SimpleGaugeWithValueAndLabelElementComponent';

export class ViewSimpleGaugeWithValueAndLabelPanel implements m.ClassComponent<SimpleGaugeWithValueAndLabelPanelAttrs> {
  view({attrs}: m.Vnode<SimpleGaugeWithValueAndLabelPanelAttrs>): void | m.Children {
    return m(".panel", [
      m("a.editLink", {
        href:attrs.editLink
      }, m(EditDocumentIcon)),
      m(".title", attrs.element.title),
      m("br", {clear:"both"}),
      (attrs.element.content||[]).map(pi => m(".editableBlock", [
        m(SimpleGaugeWithValueAndLabelElementComponent, {
          currentData:MetricsReader.getLastResponse(),
          parent:null,
          model:pi
        }),
      ]))
    ]);
  }
}

export interface SimpleGaugeWithValueAndLabelPanelAttrs {
  element:SimpleGaugeWithValueAndLabelPanel;
  editLink:string;
}

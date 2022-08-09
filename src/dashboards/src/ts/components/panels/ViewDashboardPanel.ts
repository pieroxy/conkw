import m from 'mithril';
import { DashboardPanel, GaugeWithHistoryElement, SimpleGaugeWithValueAndLabelElement, TopLevelPanelElement, TopLevelPanelElementEnum } from '../../auto/pieroxy-conkw';
import { MetricsReader } from '../../utils/api/MetricsReader';
import { EditDocumentIcon } from '../atoms/icons/EditDocumentIcon';
import { Link } from '../atoms/Link';
import { GaugeWithHistoryLineComponent } from '../dashboards/panelitems/GaugeWithHistoryLineComponent';
import { SimpleGaugeWithValueAndLabelElementComponent } from '../dashboards/panelitems/SimpleGaugeWithValueAndLabelElementComponent';

export class ViewDashboardPanel implements m.ClassComponent<SimpleGaugeWithValueAndLabelPanelAttrs> {
  view({attrs}: m.Vnode<SimpleGaugeWithValueAndLabelPanelAttrs>): void | m.Children {
    return m(".panel", [
      m(Link, {
        className:"editLink",
        target:attrs.editLink
      }, m(EditDocumentIcon)),
      m(".title", attrs.element.title),
      m("br", {clear:"both"}),
      (attrs.element.content||[]).map(pi => m(".editableBlock", ViewDashboardPanel.getDashboardPanel(pi)))
    ]);
  }

  public static getDashboardPanel(element:TopLevelPanelElement):m.Children {
    switch (element.type) {
      case TopLevelPanelElementEnum.SIMPLE_GAUGE:
        return m(SimpleGaugeWithValueAndLabelElementComponent, {
          currentData:MetricsReader.getLastResponse(),
          model:<SimpleGaugeWithValueAndLabelElement>element
        });
      case TopLevelPanelElementEnum.GAUGE_WITH_HISTORY:
        return m(GaugeWithHistoryLineComponent, {
          currentData:MetricsReader.getLastResponse(),
          model:<GaugeWithHistoryElement>element
        });
      default:
        return m("", "Unknown element type " + element.type);
    }
  }
}

export interface SimpleGaugeWithValueAndLabelPanelAttrs {
  element:DashboardPanel;
  editLink:string;
}

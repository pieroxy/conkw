import m from 'mithril';
import { ApiEndpoints } from '../../../../auto/ApiEndpoints';
import { TopLevelPanelElement, TopLevelPanelElementEnum } from '../../../../auto/pieroxy-conkw';
import { GaugeWithHistoryLineComponent } from '../../view/panelitems/GaugeWithHistoryLineComponent';
import { SimpleGaugeWithValueAndLabelElementComponent } from '../../view/panelitems/SimpleGaugeWithValueAndLabelElementComponent';
import { DefaultGaugeWithHistoryLine } from './DefaultGaugeWithHistoryLine';
import { DefaultSimpleGaugeWithValueAndLabelLine } from './DefaultSimpleGaugeWithValueAndLabelLine';

export class NewPanelItemComponent implements m.ClassComponent<NewPanelItemComponentAttrs> {

  view({attrs}:m.Vnode<NewPanelItemComponentAttrs>):m.Children {
    return [
      m(".title", "Add items to the panel on your left:"),
      m(".panel.clickable", {
        onclick:() => {
          ApiEndpoints.NewPanelItem.call({
            dashboardId:attrs.dashboardId,
            panelId:attrs.panelId,
            type:TopLevelPanelElementEnum.SIMPLE_GAUGE
          }).then((out) => {
            attrs.addNewItem(DefaultSimpleGaugeWithValueAndLabelLine.build(out.element))
          });
        }
      }, [
        m(".title", "Simple gauge with value and label"),
        m("br"),
        m(SimpleGaugeWithValueAndLabelElementComponent, DefaultSimpleGaugeWithValueAndLabelLine.getFakeData(this.getValue())),
        m("br", {clear:"both"})
      ]),
      m(".panel.clickable", {
        onclick:() => {
          ApiEndpoints.NewPanelItem.call({
            dashboardId:attrs.dashboardId,
            panelId:attrs.panelId,
            type:TopLevelPanelElementEnum.GAUGE_WITH_HISTORY
          }).then((out) => {
            attrs.addNewItem(DefaultGaugeWithHistoryLine.build(out.element))
          });
        }
      }, [
        m(".title", "Gauge with history & value & label"),
        m("br"),
        m(GaugeWithHistoryLineComponent, DefaultGaugeWithHistoryLine.getFakeData(this.getValue())),
        m("br", {clear:"both"})
      ])
    ];
  }



  getValue(): number {
    return (Math.sin(Date.now()/3000)*0.5+0.5)*100;
  }
}

export interface NewPanelItemComponentAttrs {
  dashboardId:string;
  panelId:string;
  addNewItem:(s:TopLevelPanelElement)=>void;
}
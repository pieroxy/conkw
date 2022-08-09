import m from 'mithril';
import { GaugeWithHistoryElement, SimpleGaugeWithValueAndLabelElement } from '../../../../auto/pieroxy-conkw';
import { DisplayUtils } from '../../../../utils/DisplayUtils';
import { GaugeWithHistoryComponent } from '../atoms/GaugeWithHistoryComponent';
import { TextLabelComponent } from '../atoms/TextLabel';
import { ValueWithUnitComponent } from '../atoms/ValueWithUnit';
import { DashboardElement, DashboardElementAttrs } from '../../DashboardElement';

export class GaugeWithHistoryLineComponent 
extends DashboardElement<GaugeWithHistoryLineComponentAttrs> {
  public view({attrs}:m.Vnode<GaugeWithHistoryLineComponentAttrs>):m.Children {
    return m(".simpleGaugeValueLabel", [
      m(".sgvl-gauge", m(GaugeWithHistoryComponent, {
        currentData:attrs.currentData,
        model:attrs.model.gauge,
      })),
      m(".sgvl-value", m(ValueWithUnitComponent, {
        currentData:attrs.currentData,
        model:attrs.model.value,
      })),
      m(".sgvl-label", {
        style: {
          width:"calc(100% - "+this.getRemainingWidth(attrs.model)+"px - 1px)"
        }
      }, m(TextLabelComponent, {
        currentData:attrs.currentData,
        model:attrs.model.label,
      })),
      m(".sgvl-bottom")
    ])
  }
  getRemainingWidth(model: SimpleGaugeWithValueAndLabelElement) {
    return 155 + (5+model.value.unit.length)*DisplayUtils.getMonospaceCharWidth(14);
  }
}

export interface GaugeWithHistoryLineComponentAttrs extends DashboardElementAttrs {
  model:GaugeWithHistoryElement;
}
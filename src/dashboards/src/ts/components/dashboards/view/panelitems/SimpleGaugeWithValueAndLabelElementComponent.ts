import m from 'mithril';
import { SimpleGaugeWithValueAndLabelElement } from '../../../../auto/pieroxy-conkw';
import { DisplayUtils } from '../../../../utils/DisplayUtils';
import { SimpleGaugeComponent } from '../atoms/SimpleGaugeComponent';
import { TextLabelComponent } from '../atoms/TextLabel';
import { ValueWithUnitComponent } from '../atoms/ValueWithUnit';
import { DashboardElement, DashboardElementAttrs } from '../../DashboardElement';

export class SimpleGaugeWithValueAndLabelElementComponent 
extends DashboardElement<SimpleGaugeWithValueAndLabelElementAttrs> {
  public view({attrs}:m.Vnode<SimpleGaugeWithValueAndLabelElementAttrs>):m.Children {
    return m(".simpleGaugeValueLabel", [
      m(".sgvl-gauge", m(SimpleGaugeComponent, {
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

export interface SimpleGaugeWithValueAndLabelElementAttrs extends DashboardElementAttrs {
  model:SimpleGaugeWithValueAndLabelElement;
}
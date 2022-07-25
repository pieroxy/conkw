import m from 'mithril';
import { SimpleGaugeWithValueAndLabelElement } from '../../../auto/pieroxy-conkw';
import { SimpleGaugeComponent } from './atoms/SimpleGauge';
import { TextLabelComponent } from './atoms/TextLabel';
import { ValueWithUnitComponent } from './atoms/ValueWithUnit';
import { DashboardElement, DashboardElementAttrs } from './DashboardElement';

export class SimpleGaugeWithValueAndLabelElementComponent 
extends DashboardElement<SimpleGaugeWithValueAndLabelElementAttrs> {
  public view({attrs}:m.Vnode<SimpleGaugeWithValueAndLabelElementAttrs>):m.Children {
    return m(".simpleGaugeValueLabel", [
      m(".sgvl-label", m(TextLabelComponent, {
        currentData:attrs.currentData,
        model:attrs.model.label,
        parent:this,
      })),
      m(".sgvl-value", m(ValueWithUnitComponent, {
        currentData:attrs.currentData,
        model:attrs.model.value,
        parent:this,
      })),
      m(".sgvl-gauge", m(SimpleGaugeComponent, {
        currentData:attrs.currentData,
        model:attrs.model.gauge,
        parent:this,
      })),
    ])
  }
}

export interface SimpleGaugeWithValueAndLabelElementAttrs extends DashboardElementAttrs {
  model:SimpleGaugeWithValueAndLabelElement;
}
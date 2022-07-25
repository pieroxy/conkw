import m from 'mithril';
import { SimpleGauge } from '../../../../auto/pieroxy-conkw';
import { DashboardElement, DashboardElementAttrs } from '../DashboardElement';

export class SimpleGaugeComponent extends DashboardElement<SimpleGaugeAttrs> {
  public view({attrs}:m.Vnode<SimpleGaugeAttrs>):m.Children {
    let ee = attrs.model.errorValue;
    let errorElement = ee ? m(".threshold." + (ee.directive === "valueabove" ? "left" : "right")) : null;
    let min = this.computeNumericValue(attrs.model.min, attrs.currentData);
    let max = this.computeNumericValue(attrs.model.max, attrs.currentData);
    let value = this.computeNumericValue(attrs.model.value, attrs.currentData);
    
    return m(".gauge", [
      errorElement,
      m(".gaugethumb", {
        style: {
          width: ((value - min) * 100 / (max - min)) + "%"
        }
      })
    ]);
  }
}

export interface SimpleGaugeAttrs extends DashboardElementAttrs {
  model:SimpleGauge;
}
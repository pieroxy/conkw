import m from 'mithril';
import { SimpleGauge } from '../../../../auto/pieroxy-conkw';
import { NumberUtils } from '../../../../utils/NumberUtils';
import { DashboardElement, DashboardElementAttrs } from '../../DashboardElement';

export class SimpleGaugeComponent extends DashboardElement<SimpleGaugeAttrs> {
  private fakeSeed=0;

  public view({attrs}:m.Vnode<SimpleGaugeAttrs>):m.Children {
    let ee = attrs.model.errorValue;
    let errorElement = ee ? m(".threshold." + (ee.directive === "valueabove" ? "left" : "right")) : null;
    let min = this.computeNumericValue(attrs.model.min, attrs.currentData.rawData);
    let max = this.computeNumericValue(attrs.model.max, attrs.currentData.rawData);
    let value = this.computeNumericValue(attrs.model.value, attrs.currentData.rawData);
    if (attrs.currentData.useFakeDemoData) {
      value = Math.sin((this.fakeSeed++)/10)*50+50;
    }
    
    return m(".gauge" + this.getStaleClass(attrs.model.value, attrs.model.staleDelay), [
      errorElement,
      m(".gaugethumb", {
        style: {
          width: NumberUtils.computePercentage(value, min, max, attrs.model.logarithmic) + "%"
        }
      })
    ]);
  }
}

export interface SimpleGaugeAttrs extends DashboardElementAttrs {
  model:SimpleGauge;
}
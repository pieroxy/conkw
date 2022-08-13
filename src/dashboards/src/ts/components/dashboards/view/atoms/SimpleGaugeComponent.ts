import m from 'mithril';
import { SimpleGauge, WarningDirective } from '../../../../auto/pieroxy-conkw';
import { NumberUtils } from '../../../../utils/NumberUtils';
import { DashboardElement, DashboardElementAttrs } from '../../DashboardElement';

export class SimpleGaugeComponent extends DashboardElement<SimpleGaugeAttrs> {

  public view({attrs}:m.Vnode<SimpleGaugeAttrs>):m.Children {
    let model = attrs.model;
    let ee = model.errorValue;
    let errorElement = ee ? m(".threshold." + (ee.directive === "valueabove" ? "left" : "right")) : null;
    let min = this.computeNumericValue(model.min, attrs.currentData.rawData);
    let max = this.computeNumericValue(model.max, attrs.currentData.rawData);
    let value = this.computeNumericValue(model.value, attrs.currentData.rawData);
    if (attrs.currentData.useFakeDemoData) {
      value = attrs.currentData.useFakeDemoData.value;
    }
    let warnValue = model.error===undefined ? NaN : this.computeNumericValue(model.error, attrs.currentData.rawData);
    let above = model.error && model.error.directive === WarningDirective.VALUEABOVE;
    let below = model.error && model.error.directive === WarningDirective.VALUEBELOW;
    let className = ".gauge";
    className += this.getStaleClass(model.value, attrs.model.staleDelay);
    className += this.getValueWarningClass(model.error, value, warnValue);
    
    return m(className, [
      errorElement,
      above ? m(".gaugewarningright", {style:{width:(100-(warnValue - min) * 100 / (max - min)) + "%"}}) : null,
      m(".gaugethumb", {
        style: {
          width: NumberUtils.computePercentage(value, min, max, model.logarithmic) + "%"
        }
      }),
      below ? m(".gaugewarningleft", {style:{width:(((warnValue - min) * 100 / (max - min)) + "%")}}) : null,
    ]);
  }
}

export interface SimpleGaugeAttrs extends DashboardElementAttrs {
  model:SimpleGauge;
}
import m from 'mithril';
import { DashboardDynamicValue } from '../../../../auto/pieroxy-conkw';
import { NumberUtils } from '../../../../utils/NumberUtils';
import { DashboardElement, DashboardElementAttrs } from '../../DashboardElement';

export class ValueWithUnitComponent extends DashboardElement<ValueWithUnitAttrs> {

  public view({attrs}:m.Vnode<ValueWithUnitAttrs>):m.Children {
    let value = this.computeNumericValue(attrs.model.value, attrs.currentData.rawData);
    let unit = attrs.model.value.directive?.unit || "";
    if (attrs.currentData.useFakeDemoData) {
      value = attrs.currentData.useFakeDemoData.value;
    }
    let warnValue = attrs.model.error===undefined ? NaN : this.computeNumericValue(attrs.model.error, attrs.currentData.rawData);
    let className = ".silabel";
    className += this.getStaleClass(attrs.model.value, attrs.model.staleDelay);
    className += this.getValueWarningClass(attrs.model.error, value, warnValue);

    return m(className, NumberUtils.getSI(value) + unit);
  }
}

export interface ValueWithUnitAttrs extends DashboardElementAttrs {
  model:DashboardDynamicValue;
}
import m from 'mithril';
import { SiPrefixedValue } from '../../../../auto/pieroxy-conkw';
import { NumberUtils } from '../../../../utils/NumberUtils';
import { DashboardElement, DashboardElementAttrs } from '../DashboardElement';

export class ValueWithUnitComponent extends DashboardElement<ValueWithUnitAttrs> {
  public view({attrs}:m.Vnode<ValueWithUnitAttrs>):m.Children {
    let value = this.computeNumericValue(attrs.model.value, attrs.currentData.rawData);
    let unit = attrs.model.unit;
    
    return m(".silabel", NumberUtils.getSI(value) + unit);
  }
}

export interface ValueWithUnitAttrs extends DashboardElementAttrs {
  model:SiPrefixedValue;
}
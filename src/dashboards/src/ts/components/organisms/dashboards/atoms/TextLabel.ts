import m from 'mithril';
import { DynamicLabel } from '../../../../auto/pieroxy-conkw';
import { DashboardElement, DashboardElementAttrs } from '../DashboardElement';

export class TextLabelComponent extends DashboardElement<TextLabelAttrs> {
  public view({attrs}:m.Vnode<TextLabelAttrs>):m.Children {
    let value = this.computeTextValue(attrs.model.value, attrs.currentData.rawData);
    
    return m(".label" + this.getStaleClass(attrs.model.value, attrs.model.staleDelay), value);
  }
}

export interface TextLabelAttrs extends DashboardElementAttrs {
  model:DynamicLabel;
}
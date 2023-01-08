import m from 'mithril';
import { ExpressionValueType, LabelAndValueElement } from '../../../../auto/pieroxy-conkw';
import { TextLabelComponent } from '../atoms/TextLabel';
import { ValueWithUnitComponent } from '../atoms/ValueWithUnit';
import { DashboardElement, DashboardElementAttrs } from '../../DashboardElement';

export class LabelAndValueComponent 
extends DashboardElement<LabelAndValueComponentAttrs> {
  public view({attrs}:m.Vnode<LabelAndValueComponentAttrs>):m.Children {
    return m(".simpleValueLabel", [
      m(".cw-value", m(attrs.model.value.value.type === ExpressionValueType.NUMERIC ? ValueWithUnitComponent : TextLabelComponent, {
        currentData:attrs.currentData,
        model:attrs.model.value,
      })),
      m(".cw-label", m(TextLabelComponent, {
        currentData:attrs.currentData,
        model:attrs.model.label,
      })),
      m(".cw-bottom")
    ])
  }
}

export interface LabelAndValueComponentAttrs extends DashboardElementAttrs {
  model:LabelAndValueElement;
}
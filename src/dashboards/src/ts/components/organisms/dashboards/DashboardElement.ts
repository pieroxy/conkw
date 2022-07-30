import m from 'mithril';
import { DashboardDynamicElement, ExpressionClass, MetricsApiResponse, ValueExpression } from '../../../auto/pieroxy-conkw';

export abstract class DashboardElement<T extends DashboardElementAttrs> implements m.ClassComponent<T> {

  private parent:DashboardElement<any>|null;
  private model:DashboardDynamicElement;

  oninit({attrs}:m.Vnode<T>) {
    this.parent = attrs.parent;
    this.model = attrs.model;
  }

  getNamespace():string {
    if (this.model.namespace) return this.model.namespace;
    
    return this.parent ? this.parent.getNamespace() : "namespace not found";
  }

  computeNumericValue(expression:ValueExpression, data:MetricsApiResponse):number {
    switch (expression.clazz) {
      case ExpressionClass.LITERAL:
        return parseFloat(expression.value);
      case ExpressionClass.METRIC:
        let ns = this.getNamespace();
        if (data.metrics && data.metrics[ns] && data.metrics[ns].num)
          return data.metrics[ns].num[expression.value];
        else
          return NaN;
    }
    return -1.02;
  }

  computeTextValue(expression:ValueExpression, data:MetricsApiResponse):string|null {
    switch (expression.clazz) {
      case ExpressionClass.LITERAL:
        return expression.value;
      case ExpressionClass.METRIC:
        let ns = this.getNamespace();
        if (data.metrics && data.metrics[ns] && data.metrics[ns].num)
          return data.metrics[ns].str[expression.value];
        else
          return null;
    }
    return "-1.03";
  }

  abstract view(vnode: m.Vnode<T>): m.Children;
}

export interface DashboardElementAttrs {
  currentData:MetricsApiResponse;
  parent:DashboardElement<any>|null;
  model:DashboardDynamicElement;
}
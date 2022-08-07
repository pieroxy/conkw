import m from 'mithril';
import { DashboardDynamicElement, ExpressionClass, MetricsApiResponse, ValueExpression } from '../../../auto/pieroxy-conkw';
import { MetricsReader } from '../../../utils/api/MetricsReader';

export abstract class DashboardElement<T extends DashboardElementAttrs> implements m.ClassComponent<T> {

  computeNumericValue(expression:ValueExpression, data:MetricsApiResponse):number {
    switch (expression.clazz) {
      case ExpressionClass.LITERAL:
        return parseFloat(expression.value);
      case ExpressionClass.METRIC:
        let ns = expression.namespace;
        if (ns) MetricsReader.didReadFromGrabber(ns);
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
        let ns = expression.namespace;
        if (ns) MetricsReader.didReadFromGrabber(ns);
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
  model:DashboardDynamicElement;
}
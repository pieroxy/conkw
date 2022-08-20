import m from 'mithril';
import { DashboardDynamicElement, ExpressionClass, MetricsApiResponse, NumberDirective, NumberOperator, ValueExpression, WarningDirective } from '../../auto/pieroxy-conkw';
import { CurrentData } from '../../utils/api/CurrentData';
import { MetricsReader } from '../../utils/api/MetricsReader';

export abstract class DashboardElement<T extends DashboardElementAttrs> implements m.ClassComponent<T> {

  computeNumericValue(expression:ValueExpression, data:MetricsApiResponse):number {
    if (!expression) return NaN;
    let result = -1.02;
    switch (expression.clazz) {
      case ExpressionClass.LITERAL:
        result = parseFloat(expression.value);
        break;
      case ExpressionClass.METRIC:
        let ns = expression.namespace;
        if (ns) MetricsReader.didReadFromGrabber(ns);
        if (data.metrics && data.metrics[ns] && data.metrics[ns].num)
          result = data.metrics[ns].num[expression.value];
        else
          result = NaN;
        break;
    }
    return this.applyNumericTransform(result, expression.directive);
  }

  applyNumericTransform(result: number, directive?: NumberDirective): number {
    if (directive?.operator && directive.value !== undefined) {
      switch (directive.operator) {
        case NumberOperator.MULT:
          return result * directive.value;
        case NumberOperator.MULT:
          return result / directive.value;
      }
    }
    return result;
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

  getStaleClass(expression:ValueExpression, staleDelay:number):string {
    let acs = "";
    if (expression.clazz === ExpressionClass.METRIC) {
      if (MetricsReader.getMetricAge(expression.namespace, expression.value) > staleDelay) {
        acs = ".cw-stale";
      }
    }
    return acs;
  }

  getValueWarningClass(error: ValueExpression | undefined, value: number, limitValue:number) {
    if (!error) return "";
    let warning = false;
    switch (error.directive?.test) {
      case WarningDirective.VALUEABOVE:
        warning = value > limitValue;
        break;
      case WarningDirective.VALUEBELOW:
        warning = value < limitValue;
        break;
    }
    return warning ? ".cw-error" : "";
  }

  abstract view(vnode: m.Vnode<T>): m.Children;
}

export interface DashboardElementAttrs {
  currentData:CurrentData;
  model:DashboardDynamicElement;
}
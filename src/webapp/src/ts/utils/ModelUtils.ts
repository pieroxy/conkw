import { ValueExpression } from "../auto/pieroxy-conkw";

export class ModelUtils {
  static copyValueExpression(src:ValueExpression, dest:ValueExpression) {
    dest.clazz = src.clazz;
    dest.directive = src.directive;
    dest.namespace = src.namespace;
    dest.type = src.type;
    dest.value = src.value;
  }
}
import { ExpressionClass, ExpressionValueType, SimpleGaugeWithValueAndLabelElement, TopLevelPanelElementEnum, WarningDirective } from "../../../../auto/pieroxy-conkw";
import { SimpleGaugeWithValueAndLabelElementAttrs } from "../../view/panelitems/SimpleGaugeWithValueAndLabelElementComponent";

export class DefaultSimpleGaugeWithValueAndLabelLine {
  public static getFakeData(value:number): SimpleGaugeWithValueAndLabelElementAttrs {
    return {
      currentData:{
        iteration:1,
        rawData:{
          metrics: {},
          timestamp: 0,
          instanceName: "string",
          responseJitter: 0,
          needsAuthentication: false,
          errors: [],
          numCount: 0,
          strCount: 0,
        },
        metricGap:false,
        useFakeDemoData:{
          value: value
        }
      },
      model: <SimpleGaugeWithValueAndLabelElement>{ 
        gauge: {
          min: {
            namespace:"",
            clazz: ExpressionClass.LITERAL,
            type:ExpressionValueType.NUMERIC,
            value:"0"
          },
          max: {
            namespace:"",
            clazz: ExpressionClass.LITERAL,
            type:ExpressionValueType.NUMERIC,
            value:"100"
          },
          value: {
            namespace:"",
            clazz: ExpressionClass.LITERAL,
            type:ExpressionValueType.NUMERIC,
            directive:{},
            value:"75"
          }
        },
        label: {
          value: {
            namespace:"",
            clazz: ExpressionClass.LITERAL,
            type:ExpressionValueType.STRING,
            value:"CPU Usage"
          }
        },
        value: {
          value: {
            namespace:"",
            clazz: ExpressionClass.LITERAL,
            type:ExpressionValueType.NUMERIC,
            value:"75"
          },
          unit:"%",
          thousand:1000
        }
      }
    }
  }

  static build(_element:any):SimpleGaugeWithValueAndLabelElement {
    let element:SimpleGaugeWithValueAndLabelElement=_element;
    element.valueIsGauge=true,
    element.label= {
      staleDelay:5,
      value: {
        clazz:ExpressionClass.LITERAL,
        type:ExpressionValueType.STRING,
        value:"The label",
        namespace:""
      }
    };
    element.value= {
      staleDelay:5,
      thousand:1000,
      unit:"",
      value: {
        clazz:ExpressionClass.LITERAL,
        type:ExpressionValueType.NUMERIC,
        value:"35",
        namespace:"",
      }
    };
    element.gauge= {
      staleDelay:5,
      logarithmic:false,
      value: {
        clazz:ExpressionClass.LITERAL,
        type:ExpressionValueType.NUMERIC,
        value:"35",
        namespace:"",
      },
      min: {
        clazz:ExpressionClass.LITERAL,
        type:ExpressionValueType.NUMERIC,
        value:"0",
        namespace:"",
      },
      max: {
        clazz:ExpressionClass.LITERAL,
        type:ExpressionValueType.NUMERIC,
        value:"100",
        namespace:"",
      },
      error:{
        clazz:ExpressionClass.LITERAL,
        namespace:"",
        type:ExpressionValueType.NUMERIC,
        value:"75",
        directive:{
          test:WarningDirective.VALUEABOVE
        }
      }
    };
    element.type=TopLevelPanelElementEnum.SIMPLE_GAUGE;
    return element;
  }
}
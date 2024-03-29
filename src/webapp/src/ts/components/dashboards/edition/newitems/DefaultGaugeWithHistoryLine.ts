import { ExpressionClass, ExpressionValueType, GaugeWithHistoryElement, TopLevelPanelElementEnum, WarningDirective } from "../../../../auto/pieroxy-conkw";
import { GaugeWithHistoryLineComponentAttrs } from "../../view/panelitems/GaugeWithHistoryLineComponent";

export class DefaultGaugeWithHistoryLine {
  static getFakeData(value:number): GaugeWithHistoryLineComponentAttrs {
    return {
      currentData:{
        iteration:new Date().getSeconds(),
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
      model: <GaugeWithHistoryElement>{ 
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
          },
          nbLinesHeight:2
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
            value:"75",
            directive: {
              unit:"%",
            }
          },
        }
      }
    }
  }

  static build(_element:any):GaugeWithHistoryElement {
    let element : GaugeWithHistoryElement = _element;
    element.valueIsGauge=true;
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
      value: {
        clazz:ExpressionClass.LITERAL,
        type:ExpressionValueType.NUMERIC,
        value:"35",
        namespace:"",
      }
    };
    element.gauge= {
      nbLinesHeight: 2,
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
    element.type=TopLevelPanelElementEnum.GAUGE_WITH_HISTORY;
    return element;
  }
}
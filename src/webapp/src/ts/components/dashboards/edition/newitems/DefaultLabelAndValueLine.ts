import { ExpressionClass, ExpressionValueType, LabelAndValueElement, SimpleGaugeWithValueAndLabelElement, TopLevelPanelElementEnum } from "../../../../auto/pieroxy-conkw";
import { LabelAndValueComponentAttrs } from "../../view/panelitems/LabelAndValueComponent";

export class DefaultLabelAndValueLine {
  public static getFakeData(value:number): LabelAndValueComponentAttrs {
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
            directive:{
              unit:"%",
            }
          },
        }
      }
    }
  }

  static build(_element:any):LabelAndValueElement {
    let element:LabelAndValueElement=_element;
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
    element.type=TopLevelPanelElementEnum.LABEL_VALUE;
    return element;
  }
}
import m from 'mithril';
import { ApiEndpoints } from '../../../auto/ApiEndpoints';
import { SimpleGaugeWithValueAndLabelElementAttrs, SimpleGaugeWithValueAndLabelElementComponent } from '../../organisms/dashboards/SimpleGaugeWithValueAndLabelElementComponent';
import { ExpressionClass, ExpressionValueType, GaugeWithHistoryElement, SimpleGaugeWithValueAndLabelElement, TopLevelPanelElementEnum } from '../../../auto/pieroxy-conkw';
import { GaugeWithHistoryLineComponent, GaugeWithHistoryLineComponentAttrs } from '../../organisms/dashboards/GaugeWithHistoryLineComponent';

export class NewPanelItemComponent implements m.ClassComponent<NewPanelItemComponentAttrs> {

  view({attrs}:m.Vnode<NewPanelItemComponentAttrs>):m.Children {
    return [
      m(".title", "Add items to the panel on your left:"),
      m(".panel.clickable", {
        onclick:() => {
          ApiEndpoints.NewPanelItem.call({
              dashboardId:attrs.dashboardId,
              panelId:attrs.panelId,
              type:TopLevelPanelElementEnum.SIMPLE_GAUGE
            }
          ).then(() => {
            attrs.refreshData();
          });
        }
      }, [
        m(".title", "Simple gauge with value and label"),
        m("br"),
        m(SimpleGaugeWithValueAndLabelElementComponent, this.getFakeSimpleGaugeData()),
        m("br", {clear:"both"})
      ]),
      m(".panel.clickable", {
        onclick:() => {
          ApiEndpoints.NewPanelItem.call({
              dashboardId:attrs.dashboardId,
              panelId:attrs.panelId,
              type:TopLevelPanelElementEnum.GAUGE_WITH_HISTORY
            }
          ).then(() => {
            attrs.refreshData();
          });
        }
      }, [
        m(".title", "Gauge with history & value & label"),
        m("br"),
        m(GaugeWithHistoryLineComponent, this.getFakeHistoryGaugeData()),
        m("br", {clear:"both"})
      ])
    ];
  }

  getFakeSimpleGaugeData(): SimpleGaugeWithValueAndLabelElementAttrs {
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
        useFakeDemoData:true
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
            directive:"",
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
  
  getFakeHistoryGaugeData(): GaugeWithHistoryLineComponentAttrs {
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
        useFakeDemoData:true
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
            directive:"",
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
}

export interface NewPanelItemComponentAttrs {
  dashboardId:string;
  panelId:string;
  refreshData:()=>void;
}
import { CallApiInput, CallApiOutput, GetGrabbersInput, GetGrabbersOutput, IdLabelPair, MetricsApiResponse } from "../../auto/pieroxy-conkw";
import { Api } from "./Api";

export class MetricsReader {
  private static GRABBERS_LIST_TTL = 10000;
  private static GRABBERS_TTL = 5000;

  private static lastResponse:MetricsApiResponse = {
    errors:[],
    instanceName:"placeholder",
    metrics:{},
    needsAuthentication:false,
    numCount:0,
    responseJitter:0,
    strCount:0,
    timestamp:Date.now()
  };
  private static grabbersRequested:Map<string,number> = new Map();
  private static interval?:number;
  private static grabbers:IdLabelPair[] = [];
  private static grabbersLastFetch:number = 0;

  public static didReadFromGrabber(grabber:string):void {
    MetricsReader.grabbersRequested.set(grabber, Date.now());
  }

  public static getLastResponse():MetricsApiResponse {
    if (MetricsReader.interval === undefined) {
      MetricsReader.interval = window.setInterval(() => {MetricsReader.fetch()}, 1000);
    }
    return MetricsReader.lastResponse;
  }

  public static fetch():void {
    let now = Date.now();
    let expiredBefore = now - MetricsReader.GRABBERS_TTL;
    let it = MetricsReader.grabbersRequested.keys();
    let next;
    while (next = it.next()) {
      if (next.done) break;
      let age = MetricsReader.grabbersRequested.get(next.value);
      if (age && age<expiredBefore) {
        MetricsReader.grabbersRequested.delete(next.value);
      }
    }

    Api.call<CallApiInput, CallApiOutput>({
      method:"GET",
      body:{grabbers:[...MetricsReader.grabbersRequested.keys()]},
      endpoint:"CallApi",
      hideFromSpinner:true
    }).then((o => MetricsReader.lastResponse = o.data))
  }

  static getGrabbers():IdLabelPair[] {
    if (MetricsReader.grabbersLastFetch < Date.now()-MetricsReader.GRABBERS_LIST_TTL) {
      MetricsReader.grabbersLastFetch = Date.now();
      Api.call<GetGrabbersInput, GetGrabbersOutput>({
        method:"GET",
        endpoint:"GetGrabbers",
        body: {},
        hideFromSpinner:true
      }).then((out) => {
        MetricsReader.grabbers = out.grabbers;
      })
    }
    return MetricsReader.grabbers;
  }

  static getNumMetricsList(namespace: string): string[] {
    return this.getMetricsList(namespace, "num");
  }

  static getStrMetricsList(namespace: string): string[] {
    return this.getMetricsList(namespace, "str");
  }

  private static getMetricsList(namespace: string, space:"num"|"str"): string[] {
    let rd = this.lastResponse.metrics[namespace];
    if (!rd) return [];
    var name, result = [];
    let obj = rd[space];
    if (!obj) return [];    

    for (name in obj) {
        if (obj.hasOwnProperty(name)) {
            result.push(name);
        }
    }
    return result.sort();
  }

}
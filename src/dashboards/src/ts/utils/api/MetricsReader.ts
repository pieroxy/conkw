import { GetGrabbersInput, GetGrabbersOutput, IdLabelPair, MetricsApiResponse } from "../../auto/pieroxy-conkw";
import { Api } from "./Api";

export class MetricsReader {
  static lastResponse:MetricsApiResponse = {
    errors:[],
    instanceName:"placeholder",
    metrics:{},
    needsAuthentication:false,
    numCount:0,
    responseJitter:0,
    strCount:0,
    timestamp:Date.now()
  };
  static grabbersRequested:Map<string,number> = new Map();
  static interval?:number;
  static grabbers:IdLabelPair[] = [];
  static grabbersLastFetch:number = 0;

  public static getLastResponse(grabber:string):MetricsApiResponse {
    MetricsReader.grabbersRequested.set(grabber, Date.now());
    if (MetricsReader.interval === undefined) {
      MetricsReader.interval = window.setInterval(() => {MetricsReader.fetch()}, 1000);
    }
    return MetricsReader.lastResponse;
  }

  public static fetch() {
    let now = Date.now();
    let expiredBefore = now - 5000;
    let it = MetricsReader.grabbersRequested.keys();
    let next;
    while (next = it.next()) {
      let age = MetricsReader.grabbersRequested.get(next.value);
      if (age && age<expiredBefore) {
        MetricsReader.grabbersRequested.delete(next.value);
      }
    }
    //Coming next: Api.call<any,any>()
  }
  static getGrabbers():IdLabelPair[] {
    if (MetricsReader.grabbersLastFetch < Date.now()-1000) {
      MetricsReader.grabbersLastFetch = Date.now();
      Api.call<GetGrabbersInput, GetGrabbersOutput>({
        method:"GET",
        endpoint:"GetGrabbers",
        body: {}
      }).then((out) => {
        MetricsReader.grabbers = out.grabbers;
      })
    }
    return MetricsReader.grabbers;
  }
}
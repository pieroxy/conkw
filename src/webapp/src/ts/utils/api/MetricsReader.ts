import { ApiEndpoints } from "../../auto/ApiEndpoints";
import { CallApiInput, CallApiOutput, IdLabelPair } from "../../auto/pieroxy-conkw";
import { Auth, AuthenticationStatus } from "../Auth";
import { Api } from "./Api";
import { CurrentData } from "./CurrentData";

export class MetricsReader {
  private static GRABBERS_LIST_TTL = 10000;
  private static GRABBERS_TTL = 5; // 5 iterations
  private static DEBUG = false;

  private static lastResponse:CurrentData = {
    rawData: {
      errors:[],
      instanceName:"placeholder",
      metrics:{},
      needsAuthentication:false,
      numCount:0,
      responseJitter:0,
      strCount:0,
      timestamp:Date.now()
    },
    iteration:0,
    metricGap:true,
  };
  private static lastResponseTime:number=0;
  private static grabbersRequested:Map<string,number> = new Map();
  private static interval?:number;
  private static grabbers:IdLabelPair[] = [];
  private static grabbersLastFetch:number = 0;
  private static iteration=1;
  private static lastIteration=0;

  public static didReadFromGrabber(grabber:string):void {
    MetricsReader.grabbersRequested.set(grabber, this.iteration);
  }

  public static getLastResponse():CurrentData {
    if (MetricsReader.interval === undefined) {
      MetricsReader.interval = window.setInterval(() => {MetricsReader.fetchRAF()}, 1000);
      if (MetricsReader.DEBUG) console.log("Starting metrics fetch timer");
    }
    return MetricsReader.lastResponse;
  }
  public static fetchRAF():void {
    window.requestAnimationFrame(()=>{MetricsReader.fetch()});
  }
  public static fetch():void {
    if (MetricsReader.lastIteration == MetricsReader.iteration) return;
    MetricsReader.lastIteration = MetricsReader.iteration
    let iteration = this.iteration;
    let expiredBefore = iteration - MetricsReader.GRABBERS_TTL;
    let it = MetricsReader.grabbersRequested.keys();
    let next;
    while (next = it.next()) {
      if (next.done) break;
      let age = MetricsReader.grabbersRequested.get(next.value);
      if (age && age<expiredBefore) {
        MetricsReader.grabbersRequested.delete(next.value);
      }
    }

    if (Auth.getAuthenticationStatus() !== AuthenticationStatus.LOGGED_IN ||
      MetricsReader.grabbersRequested.size < 1) {
      window.clearInterval(MetricsReader.interval);
      if (MetricsReader.DEBUG) console.log("Stopping metrics fetch timer");
      this.iteration++;
      MetricsReader.interval = undefined;
      return;
    }

    Api.call<CallApiInput, CallApiOutput>({
      method:"GET",
      body:{grabbers:[...MetricsReader.grabbersRequested.keys()]},
      endpoint:"CallApi",
      hideFromSpinner:true,
      redrawOnError:true,
      onerror:()=>{
        MetricsReader.iteration++;
      }
    }).then((o => {
      let now = Date.now();
      MetricsReader.iteration++;
  
      MetricsReader.lastResponse = {rawData:o.data, iteration:iteration, metricGap:now-this.lastResponseTime>5000};
      this.lastResponseTime = now;
    }))
  }

  static getGrabbers():IdLabelPair[] {
    if (MetricsReader.grabbersLastFetch < Date.now()-MetricsReader.GRABBERS_LIST_TTL) {
      MetricsReader.grabbersLastFetch = Date.now();
      ApiEndpoints.GetGrabbers.call({}).then((out) => {
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
    let rd = this.lastResponse.rawData.metrics[namespace];
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

  public static getMetricAge(namespace:string, metric:string):number {
    var now = Date.now() / 1000;
    let data = this.lastResponse.rawData.metrics[namespace];
    if (!data) return Number.MAX_SAFE_INTEGER;
    var mlv = data.num["ts:" + metric];
    if (mlv === undefined) mlv = data.timestamp;
    if (mlv === undefined) mlv = this.lastResponse.rawData.timestamp;
    if (mlv === undefined) mlv = Date.now();
    return now - mlv / 1000;
  }
}
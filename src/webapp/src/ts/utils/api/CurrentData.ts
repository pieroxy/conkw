import { MetricsApiResponse } from "../../auto/pieroxy-conkw";

export class CurrentData {
  rawData:MetricsApiResponse;
  iteration:number;
  metricGap:boolean;
  useFakeDemoData?:{value:number}
}
import { ClientInfo } from "../../auto/pieroxy-conkw";

export interface ApiParams<T> {
  method: "GET" | "POST";
  content: T;
  client:ClientInfo;
  url:string;
  hideFromSpinner:boolean;
}

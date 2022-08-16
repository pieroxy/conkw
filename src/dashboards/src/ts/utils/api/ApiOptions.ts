export interface ApiOptions<T> {
  method:"GET"|"POST";
  endpoint:string;
  body:T;
  hideFromSpinner?:boolean;
  redrawOnError?:boolean;
  onerror?:()=>void
}
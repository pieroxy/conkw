export interface ApiOptions<T> {
  method:"GET"|"POST";
  endpoint:string;
  body:T;
}
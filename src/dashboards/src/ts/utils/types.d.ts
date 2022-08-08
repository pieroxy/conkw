export interface ApiResponse<T> {
  code:string;
  message:string;
  content:T;
  httpCode:number;
}
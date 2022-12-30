export interface ApiResponse<T> {
  code:string;
  message:string;
  content:T;
  httpCode:number;
}

export interface StatusMessageInterface {
  status:Status;
  message:string;
}

export enum Status {
  OK,
  INFO,
  WARNING,
  ERROR
}